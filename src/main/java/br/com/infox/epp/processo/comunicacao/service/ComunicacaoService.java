package br.com.infox.epp.processo.comunicacao.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.SystemException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.transaction.Transaction;
import org.joda.time.DateTime;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.pdf.PdfManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.system.Parametros;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;

@Name(ComunicacaoService.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
@Transactional
public class ComunicacaoService {
	
	public static final String NAME = "comunicacaoService";
	
	@In
	private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
	@In
	private FluxoManager fluxoManager;
	@In
	private String codigoFluxoComunicacao;
	@In
	private String codigoFluxoComunicacaoNaoEletronico;
	@In
	private DocumentoBinarioManager documentoBinarioManager;
	@In
	private PdfManager pdfManager;
	@In
	private IniciarProcessoService iniciarProcessoService;
	@In
	private DocumentoBinManager documentoBinManager;
	@In
	private ProcessoManager processoManager;
	@In
	private GenericManager genericManager;
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In
	private PrazoComunicacaoService prazoComunicacaoService;
	@In
	private DocumentoComunicacaoService documentoComunicacaoService;
	@In
	private UsuarioLoginManager usuarioLoginManager;
	
	public void expedirComunicacao(ModeloComunicacao modeloComunicacao) throws DAOException {
		Long processIdOriginal = BusinessProcess.instance().getProcessId();
		Long taskIdOriginal = BusinessProcess.instance().getTaskId();
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			expedirComunicacao(destinatario);
		}
		BusinessProcess.instance().setProcessId(processIdOriginal);
		BusinessProcess.instance().setTaskId(taskIdOriginal);
	}
	
	public void expedirComunicacao(DestinatarioModeloComunicacao destinatario) throws DAOException {
		ModeloComunicacao modeloComunicacao = destinatario.getModeloComunicacao();
		
		Processo processo = new Processo();
		processo.setLocalizacao(Authenticator.getLocalizacaoAtual());
		processo.setNaturezaCategoriaFluxo(getNaturezaCategoriaFluxo(destinatario));
		processo.setNumeroProcesso("");
		processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
		processo.setProcessoPai(modeloComunicacao.getProcesso());
		processo.setDataInicio(DateTime.now().toDate());
		processo.setUsuarioCadastro(Authenticator.getUsuarioLogado());
		processoManager.persist(processo);

		
		Long processIdOriginal = BusinessProcess.instance().getProcessId(); // Para caso tenha sido expedido para apenas um destinatário
		Long taskIdOriginal = BusinessProcess.instance().getTaskId();
		BusinessProcess.instance().setProcessId(null);
		BusinessProcess.instance().setTaskId(null);
		iniciarProcessoService.iniciarProcesso(processo, createVariaveisJbpm(destinatario));
		BusinessProcess.instance().setProcessId(processIdOriginal);
		BusinessProcess.instance().setTaskId(taskIdOriginal);

		criarMetadados(destinatario, processo);
		
		destinatario.setExpedido(true);
		genericManager.update(destinatario);
	}
	
	public byte[] gerarPdfCompleto(ModeloComunicacao modeloComunicacao, DestinatarioModeloComunicacao destinatario) throws DAOException {
		ByteArrayOutputStream pdf = new ByteArrayOutputStream();
		try {
			ByteArrayOutputStream pdfComunicacao = gerarByteArrayComunicacao(modeloComunicacao, destinatario);
			
			com.lowagie.text.Document pdfDocument = new com.lowagie.text.Document();
			PdfCopy copy = new PdfCopy(pdfDocument, pdf);
			pdfDocument.open();
			
			copy = pdfManager.copyPdf(copy, pdfComunicacao.toByteArray());
			pdfComunicacao = null;
			Integer documentoComunicacaoId = getDocumentoComunicacao(modeloComunicacao).getId();			
			for (DocumentoModeloComunicacao documentoModelo : modeloComunicacao.getDocumentos()) {
				if (documentoModelo.getDocumento().getId() != documentoComunicacaoId){
					DocumentoBin documentoBin = documentoModelo.getDocumento().getDocumentoBin();
					if ("pdf".equals(documentoBin.getExtensao())) {
						byte[] documento = documentoBinarioManager.getData(documentoBin.getId());
						copy = pdfManager.copyPdf(copy, documento);
					} else if (documentoBin.getExtensao() == null) {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						pdfManager.convertHtmlToPdf(documentoBin.getModeloDocumento(), bos);
						copy = pdfManager.copyPdf(copy, bos.toByteArray());
					}
				}
			}
			
			pdfDocument.close();
			
			if (destinatario != null && destinatario.getExpedido()) {
				byte[] generatedPdf = pdf.toByteArray();
				pdf = new ByteArrayOutputStream();
				
				documentoBinManager.writeMargemDocumento(destinatario.getDocumentoComunicacao().getDocumentoBin(), generatedPdf, pdf);
			}
		} catch (DocumentException | IOException e) {
			rollbackAndThrow("", e);
		}
		return pdf.toByteArray();
	}
	
	public byte[] gerarPdfComunicacao(ModeloComunicacao modeloComunicacao, DestinatarioModeloComunicacao destinatario) throws DAOException {
		ByteArrayOutputStream pdf = gerarByteArrayComunicacao(modeloComunicacao, destinatario);
		if (destinatario != null && destinatario.getExpedido()) {
			byte[] generatedPdf = pdf.toByteArray();
			pdf = new ByteArrayOutputStream();
			documentoBinManager.writeMargemDocumento(destinatario.getDocumentoComunicacao().getDocumentoBin(), generatedPdf, pdf);
		}
		return pdf.toByteArray();
	}
	
	private ByteArrayOutputStream gerarByteArrayComunicacao(ModeloComunicacao modeloComunicacao, DestinatarioModeloComunicacao destinatario) throws DAOException {
		ByteArrayOutputStream pdfComunicacao = new ByteArrayOutputStream();
		try {
			String textoComunicacao = modeloComunicacao.getTextoComunicacao();
			if (textoComunicacao != null) {
				if (destinatario == null) {
					pdfManager.convertHtmlToPdf(modeloComunicacao.getTextoComunicacao(), pdfComunicacao);
				} else if (modeloComunicacao.getFinalizada()) {
					pdfManager.convertHtmlToPdf(destinatario.getDocumentoComunicacao().getDocumentoBin().getModeloDocumento(), pdfComunicacao);
				} else {
					pdfManager.convertHtmlToPdf(documentoComunicacaoService.evaluateComunicacao(destinatario), pdfComunicacao);
				}
			} else {
				DocumentoBin documentoComunicacao = getDocumentoComunicacao(modeloComunicacao).getDocumentoBin();	
				// TODO Analisar outros tipos de documento. Aqui só funciona com PDF e HTML. Mas provavelmente essa é a regra mesmo.
				byte[] doc;
				if (documentoComunicacao.isBinario()) {
					doc = documentoBinarioManager.getData(documentoComunicacao.getId());
				} else {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					pdfManager.convertHtmlToPdf(documentoComunicacao.getModeloDocumento(), bos);
					doc = bos.toByteArray();
				}
				pdfComunicacao.write(doc);
			}
		} catch (DocumentException | IOException e) {
			rollbackAndThrow("", e);
		}
		return pdfComunicacao;
	}

	private Documento getDocumentoComunicacao(ModeloComunicacao modeloComunicacao){
		if (!modeloComunicacao.getFinalizada()) {
			return documentoComunicacaoService.getDocumentoInclusoPorUsuarioInterno(modeloComunicacao).getDocumento();
		} else {
			return modeloComunicacao.getDestinatarios().get(0).getDocumentoComunicacao();
		}
	}
	
	public void finalizarComunicacao(ModeloComunicacao modeloComunicacao) throws DAOException {
		if (!modeloComunicacao.isDocumentoBinario()) {
			if (modeloComunicacao.isMinuta()) {
				rollbackAndThrow("Não é possível finalizar pois o texto no editor da comunicação é minuta", null);
			}
			if (modeloComunicacao.getClassificacaoComunicacao() == null) {
				rollbackAndThrow("Escolha a classificação de documento do editor", null);
			}
		} else if (documentoComunicacaoService.getDocumentoInclusoPorUsuarioInterno(modeloComunicacao) == null) {
			rollbackAndThrow("Deve haver texto no editor da comunicação ou pelo menos um documento incluso por usuário interno", null);
		}
		
		modeloComunicacao.setFinalizada(true);
		if (modeloComunicacao.getLocalizacaoResponsavelAssinatura() == null) {
			modeloComunicacao.setLocalizacaoResponsavelAssinatura(Authenticator.getLocalizacaoAtual());
		}
		modeloComunicacaoManager.update(modeloComunicacao);
		documentoComunicacaoService.gravarDocumentos(modeloComunicacao);
	}

	private void criarMetadados(DestinatarioModeloComunicacao destinatario, Processo processo) throws DAOException {
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processo);
		List<MetadadoProcesso> metadados = new ArrayList<>();

		criarMetadadoDestinatario(destinatario, metadadoProcessoProvider);
		
		metadados.add(metadadoProcessoProvider.gerarMetadado(
				ComunicacaoMetadadoProvider.MEIO_EXPEDICAO, destinatario.getMeioExpedicao().name()));
		
		metadados.add(metadadoProcessoProvider.gerarMetadado(
				ComunicacaoMetadadoProvider.DESTINATARIO, destinatario.getId().toString()));
		
		if (destinatario.getPrazo() != null) {
			metadados.add(metadadoProcessoProvider.gerarMetadado(
					ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO, destinatario.getPrazo().toString()));
			
		}
		
		if (destinatario.getMeioExpedicao() == MeioExpedicao.SI) {
			metadados.add(metadadoProcessoProvider.gerarMetadado(
					EppMetadadoProvider.TIPO_PROCESSO, TipoProcesso.COMUNICACAO.toString()));
		} else {
			metadados.add(metadadoProcessoProvider.gerarMetadado(
					EppMetadadoProvider.TIPO_PROCESSO, TipoProcesso.COMUNICACAO_NAO_ELETRONICA.toString()));
		}
		
		metadadoProcessoManager.persistMetadados(metadadoProcessoProvider, metadados);
		metadados = new ArrayList<>();
		if (destinatario.getModeloComunicacao().getTipoComunicacao().getQuantidadeDiasCiencia() == 0) {
			prazoComunicacaoService.darCiencia(processo, new Date(), usuarioLoginManager.find(Integer.valueOf(Parametros.ID_USUARIO_SISTEMA.getValue())));
		} else {
		    Date dataLimiteCiencia = prazoComunicacaoService.contabilizarPrazoCiencia(processo);
            metadados.add(metadadoProcessoProvider.gerarMetadado(
		            ComunicacaoMetadadoProvider.LIMITE_DATA_CIENCIA, new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(dataLimiteCiencia)));
            metadadoProcessoManager.persistMetadados(metadadoProcessoProvider, metadados);
		}
		
	}
	
	private Map<String, Object> createVariaveisJbpm(DestinatarioModeloComunicacao destinatario) {
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put(VariaveisJbpmComunicacao.MEIO_EXPEDICAO, destinatario.getMeioExpedicao().getLabel());
		variaveis.put(VariaveisJbpmComunicacao.CODIGO_MEIO_EXPEDICAO, destinatario.getMeioExpedicao().name());
		variaveis.put(VariaveisJbpmComunicacao.NOME_DESTINATARIO, destinatario.getNome());
		if(destinatario.getDestinatario() != null && destinatario.getDestinatario().getUsuarioLogin() != null){
			variaveis.put(VariaveisJbpmComunicacao.EMAIL_DESTINATARIO, destinatario.getDestinatario().getUsuarioLogin().getEmail());
		}
		variaveis.put(VariaveisJbpmComunicacao.PRAZO_DESTINATARIO, destinatario.getPrazo());
		variaveis.put(VariaveisJbpmComunicacao.TIPO_COMUNICACAO, destinatario.getModeloComunicacao().getTipoComunicacao().getDescricao());
		variaveis.put("cienciaAutomatica", destinatario.getModeloComunicacao().getTipoComunicacao().getQuantidadeDiasCiencia() == 0);
		return variaveis;
	}
	
	private NaturezaCategoriaFluxo getNaturezaCategoriaFluxo(DestinatarioModeloComunicacao destinatario) throws DAOException {
		Fluxo fluxo;
		if (destinatario.getMeioExpedicao() == MeioExpedicao.SI) {
			fluxo = fluxoManager.getFluxoByCodigo(codigoFluxoComunicacao);
		} else {
			fluxo = fluxoManager.getFluxoByCodigo(codigoFluxoComunicacaoNaoEletronico);
		}
		if (fluxo == null) {
			rollbackAndThrow("Fluxo de comunicação não encontrado", null);
		}
		List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
		if (ncfs.isEmpty()) {
			rollbackAndThrow("Não existe natureza/categoria/fluxo configurada para o fluxo de comunicação", null);
		}
		return ncfs.get(0);
	}
	
	private void criarMetadadoDestinatario(DestinatarioModeloComunicacao destinatario, MetadadoProcessoProvider metadadoProcessoProvider) throws DAOException {
		List<MetadadoProcesso> metadadosCriados = new ArrayList<>();
	    if (destinatario.getDestinatario() != null) {
			PessoaFisica pessoaDestinatario = destinatario.getDestinatario();
			MetadadoProcesso metadadoRelator = destinatario.getModeloComunicacao().getProcesso().getMetadado(EppMetadadoProvider.RELATOR);
			if (metadadoRelator != null && destinatario.getModeloComunicacao().getEnviarRelatoria()) {
				PessoaFisica relator = metadadoRelator.getValue();
				// Vai pra UDM do relator
				if (relator.equals(pessoaDestinatario)) {
					MetadadoProcesso metadadoUdm = destinatario.getModeloComunicacao().getProcesso().getMetadado(EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA);
					UnidadeDecisoraMonocratica udmRelator = metadadoUdm.getValue();
					metadadosCriados.add(metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.LOCALIZACAO_DESTINO, udmRelator.getLocalizacao().getIdLocalizacao().toString()));
				}
			} else {
				metadadosCriados.add(metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.PESSOA_DESTINATARIO, destinatario.getDestinatario().getIdPessoa().toString()));
			}
		} else {
		    metadadosCriados.add(metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.LOCALIZACAO_DESTINO, destinatario.getDestino().getIdLocalizacao().toString()));
		    if (destinatario.getPerfilDestino() != null) {
		        metadadosCriados.add(metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.PERFIL_DESTINO, destinatario.getPerfilDestino().getId().toString()));
		    }
		}
	    
	    metadadoProcessoManager.persistMetadados(metadadoProcessoProvider, metadadosCriados);
	}

	private void rollbackAndThrow(String message, Exception cause) throws DAOException {
		try {
			Transaction.instance().setRollbackOnly();
			throw new DAOException(message, cause);
		} catch (IllegalStateException | SecurityException | SystemException e) {
			throw new DAOException(e);
		}
	}
}
