package br.com.infox.epp.processo.comunicacao.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.BusinessProcess;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.pdf.PdfManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cliente.manager.CalendarioEventosManager;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.TipoModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ArbitraryExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.ExpressionResolverChain.ExpressionResolverChainBuilder;
import br.com.infox.epp.documento.type.JbpmExpressionResolver;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.DocumentoBinarioManager;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.ibpm.task.home.VariableTypeResolver;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfCopy;

@Name(ComunicacaoService.NAME)
@Scope(ScopeType.STATELESS)
@AutoCreate
public class ComunicacaoService {
	public static final String NAME = "comunicacaoService";
	
	@In
	private DocumentoManager documentoManager;
	@In
	private NaturezaCategoriaFluxoManager naturezaCategoriaFluxoManager;
	@In
	private FluxoManager fluxoManager;
	@In
	private String codigoFluxoComunicacao;
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	@In
	private DocumentoBinarioManager documentoBinarioManager;
	@In
	private PdfManager pdfManager;
	@In
	private IniciarProcessoService iniciarProcessoService;
	@In
	private DocumentoBinManager documentoBinManager;
	@In
	private VariableTypeResolver variableTypeResolver;
	@In("org.jboss.seam.bpm.jbpmContext")
	private JbpmContext jbpmContext;
	@In
	private ProcessoManager processoManager;
	@In
	private GenericManager genericManager;
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	@In
	private MetadadoProcessoManager metadadoProcessoManager;
	@In
	private CalendarioEventosManager calendarioEventosManager;
	@In
	private PapelManager papelManager;
	@In
	private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
	
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
		processoManager.persist(processo);

		processo.getMetadadoProcessoList().addAll(criarMetadados(destinatario, processo));
		for (MetadadoProcesso metadadoProcesso : processo.getMetadadoProcessoList()) {
			metadadoProcessoManager.persist(metadadoProcesso);
		}
		
		gravarDocumentos(destinatario, processo);
		
		Long processIdOriginal = BusinessProcess.instance().getProcessId(); // Para caso tenha sido expedido para apenas um destinatário
		Long taskIdOriginal = BusinessProcess.instance().getTaskId();
		BusinessProcess.instance().setProcessId(null);
		BusinessProcess.instance().setTaskId(null);
		iniciarProcessoService.iniciarProcesso(processo, createVariaveisJbpm(destinatario));
		BusinessProcess.instance().setProcessId(processIdOriginal);
		BusinessProcess.instance().setTaskId(taskIdOriginal);
		
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
			
			for (DocumentoModeloComunicacao documentoModelo : modeloComunicacao.getDocumentos()) {
				DocumentoBin documentoBin = documentoModelo.getDocumento().getDocumentoBin();
				if ("pdf".equals(documentoBin.getExtensao())) {
					byte[] documento = documentoBinarioManager.getData(documentoBin.getId());
					copy = pdfManager.copyPdf(copy, documento);
				}
			}
			
			pdfDocument.close();
			
			if (destinatario != null && destinatario.getExpedido()) {
				byte[] generatedPdf = pdf.toByteArray();
				pdf = new ByteArrayOutputStream();
				documentoBinManager.writeMargemDocumento(destinatario.getComunicacao(), generatedPdf, pdf);
			}
		} catch (DocumentException | IOException e) {
			throw new DAOException(e);
		}
		return pdf.toByteArray();
	}
	
	public byte[] gerarPdfComunicacao(ModeloComunicacao modeloComunicacao, DestinatarioModeloComunicacao destinatario) throws DAOException {
		ByteArrayOutputStream pdf = gerarByteArrayComunicacao(modeloComunicacao, destinatario);
		if (destinatario != null && destinatario.getExpedido()) {
			byte[] generatedPdf = pdf.toByteArray();
			pdf = new ByteArrayOutputStream();
			documentoBinManager.writeMargemDocumento(destinatario.getComunicacao(), generatedPdf, pdf);
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
				} else {
					pdfManager.convertHtmlToPdf(evaluateComunicacao(destinatario), pdfComunicacao);
				}
			} else {
				DocumentoBin documentoComunicacao;
				if (!modeloComunicacao.getFinalizada()) {
					documentoComunicacao = getDocumentoInclusoPorUsuarioInterno(modeloComunicacao).getDocumento().getDocumentoBin();
				} else if (destinatario != null) {
					documentoComunicacao = destinatario.getComunicacao();
				} else {
					documentoComunicacao = modeloComunicacao.getDestinatarios().get(0).getComunicacao();
				}
				byte[] doc = documentoBinarioManager.getData(documentoComunicacao.getId());
				pdfComunicacao.write(doc);
			}
		} catch (DocumentException | IOException e) {
			throw new DAOException(e);
		}
		return pdfComunicacao;
	}

	public DocumentoModeloComunicacao getDocumentoInclusoPorUsuarioInterno(ModeloComunicacao modeloComunicacao) {
		return modeloComunicacaoManager.getDocumentoInclusoPorPapel(papelManager.getIdentificadoresPapeisMembros("usuarioInterno"), modeloComunicacao);
	}
	
	public void finalizarComunicacao(ModeloComunicacao modeloComunicacao) throws DAOException {
		if (modeloComunicacao.isMinuta()) {
			throw new DAOException("Não é possível finalizar pois o texto no editor da comunicação é minuta");
		}
		String textoComunicacao = modeloComunicacao.getTextoComunicacao();
		if (textoComunicacao != null) {
			if (modeloComunicacao.getClassificacaoComunicacao() == null) {
				throw new DAOException("Escolha a classificação de documento do editor");
			}
			for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
				DocumentoBin comunicacao = documentoBinManager.createProcessoDocumentoBin("Comunicação", textoComunicacao);
				destinatario.setComunicacao(comunicacao);
			}
		} else {
			DocumentoModeloComunicacao documentoModeloComunicacao = getDocumentoInclusoPorUsuarioInterno(modeloComunicacao);
			if (documentoModeloComunicacao != null) {
				DocumentoBin comunicacao = documentoModeloComunicacao.getDocumento().getDocumentoBin();
				modeloComunicacao.setClassificacaoComunicacao(documentoModeloComunicacao.getDocumento().getClassificacaoDocumento());
				for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
					destinatario.setComunicacao(comunicacao);
				}
			} else {
				throw new DAOException("Deve haver texto no editor da comunicação ou pelo menos um documento incluso por usuário interno");
			}
		}
		modeloComunicacao.setFinalizada(true);
		modeloComunicacaoManager.update(modeloComunicacao);
	}
	
	public String evaluateComunicacao(DestinatarioModeloComunicacao destinatario) {
		DocumentoBin comunicacao = destinatario.getComunicacao();
		ModeloDocumento modeloDocumento = destinatario.getModeloComunicacao().getModeloDocumento();
		if (modeloDocumento == null) {
			return comunicacao.getModeloDocumento();
		}

		Map<String, String> variaveis = createVariaveis(destinatario);
		ProcessInstance processInstance = jbpmContext.getProcessInstance(destinatario.getModeloComunicacao().getProcesso().getIdJbpm());
		variableTypeResolver.setProcessInstance(processInstance);

		
		ExpressionResolverChain chain = ExpressionResolverChainBuilder.with(new ArbitraryExpressionResolver(variaveis))
				.and(new JbpmExpressionResolver(variableTypeResolver.getVariableTypeMap(), processInstance.getContextInstance()))
				.and(new SeamExpressionResolver()).build();
		return modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, comunicacao.getModeloDocumento(), chain);
	}
	
	public Date contabilizarPrazoCiencia(Processo comunicacao) {
		DestinatarioModeloComunicacao destinatario = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
        Integer qtdDias = destinatario.getModeloComunicacao().getTipoComunicacao().getQuantidadeDiasCiencia();
        Date hoje = new Date();
        return calendarioEventosManager.getPrimeiroDiaUtil(hoje, qtdDias);
    }
    
	public Date contabilizarPrazoCumprimento(Processo comunicacao) {
		DestinatarioModeloComunicacao destinatario = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
		MetadadoProcesso metadadoCiencia = comunicacao.getMetadado(ComunicacaoMetadadoProvider.DATA_CIENCIA);
        Integer qtdDias = destinatario.getPrazo();
        if (qtdDias == null || metadadoCiencia == null) {
        	return null;
        }
        return calendarioEventosManager.getPrimeiroDiaUtil((Date) metadadoCiencia.getValue(), qtdDias);
    }
	
	public List<ModeloDocumento> getModelosDocumentoDisponiveisComunicacao(TipoComunicacao tipoComunicacao) {
		if (tipoComunicacao == null || tipoComunicacao.getTipoModeloDocumento() == null) {
			return modeloDocumentoManager.getModeloDocumentoList();
		} else {
			TipoModeloDocumento tipoModeloDocumento = tipoComunicacao.getTipoModeloDocumento();
			return modeloDocumentoManager.getModeloDocumentoByGrupoAndTipo(tipoModeloDocumento.getGrupoModeloDocumento(), tipoModeloDocumento);
		}
	}
	
	public List<ClassificacaoDocumento> getClassificacoesDocumentoDisponiveisComunicacao(TipoComunicacao tipoComunicacao) {
		if (tipoComunicacao == null || tipoComunicacao.getClassificacaoDocumento() == null) {
			return classificacaoDocumentoFacade.getUseableClassificacaoDocumento(true);
		} else {
			return Arrays.asList(tipoComunicacao.getClassificacaoDocumento());
		}
	}
	
	private Collection<MetadadoProcesso> criarMetadados(DestinatarioModeloComunicacao destinatario, Processo processo) {
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processo);
		Collection<MetadadoProcesso> metadados = new ArrayList<>();

		metadados.add(criarMetadadoDestinatario(destinatario, metadadoProcessoProvider));
		
		metadados.add(metadadoProcessoProvider.gerarMetadado(
				ComunicacaoMetadadoProvider.MEIO_EXPEDICAO, destinatario.getMeioExpedicao().name()));
		
		metadados.add(metadadoProcessoProvider.gerarMetadado(
				ComunicacaoMetadadoProvider.DESTINATARIO, destinatario.getId().toString()));
		
		if (destinatario.getPrazo() != null) {
			metadados.add(metadadoProcessoProvider.gerarMetadado(
					ComunicacaoMetadadoProvider.PRAZO_DESTINATARIO, destinatario.getPrazo().toString()));
		}
		
		metadados.add(metadadoProcessoProvider.gerarMetadado(
				EppMetadadoProvider.TIPO_PROCESSO, TipoProcesso.COMUNICACAO.toString()));
		
		if (destinatario.getModeloComunicacao().getTipoComunicacao().getQuantidadeDiasCiencia() == 0) {
			metadados.add(metadadoProcessoProvider.gerarMetadado(
					ComunicacaoMetadadoProvider.DATA_CIENCIA, new SimpleDateFormat(MetadadoProcesso.DATE_PATTERN).format(processo.getDataInicio())));
		}
		
		return metadados;
	}
	
	private Map<String, String> createVariaveis(DestinatarioModeloComunicacao destinatario) {
		Map<String, String> variaveis = new HashMap<>();
		String format = "#'{'{0}'}'";
		
		variaveis.put(MessageFormat.format(format, MEIO_EXPEDICAO), destinatario.getMeioExpedicao().getLabel());
		variaveis.put(MessageFormat.format(format, PRAZO_DESTINATARIO), destinatario.getPrazo() != null ? destinatario.getPrazo().toString() : null);
		variaveis.put(MessageFormat.format(format, NOME_DESTINATARIO), destinatario.getNome());
		return variaveis;
	}
	
	private Map<String, Object> createVariaveisJbpm(DestinatarioModeloComunicacao destinatario) {
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put(MEIO_EXPEDICAO, destinatario.getMeioExpedicao().getLabel());
		variaveis.put(NOME_DESTINATARIO, destinatario.getNome());
		variaveis.put(PRAZO_DESTINATARIO, destinatario.getPrazo());
		return variaveis;
	}
	
	private NaturezaCategoriaFluxo getNaturezaCategoriaFluxo(DestinatarioModeloComunicacao destinatario) throws DAOException {
		Fluxo fluxo = fluxoManager.getFluxoByCodigo(codigoFluxoComunicacao);
		if (fluxo == null) {
			throw new DAOException("Fluxo de comunicação não encontrado");
		}
		List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
		if (ncfs.isEmpty()) {
			throw new DAOException("Não existe natureza/categoria/fluxo configurada para o fluxo de comunicação");
		}
		return ncfs.get(0);
	}
	
	private void gravarDocumentos(DestinatarioModeloComunicacao destinatario, Processo processoComunicacao) throws DAOException {
		DocumentoBin comunicacao = destinatario.getComunicacao();
		ModeloComunicacao modeloComunicacao = destinatario.getModeloComunicacao();
		Documento documentoComunicacao = documentoManager.createDocumento(processoComunicacao, comunicacao.getNomeArquivo(), comunicacao, modeloComunicacao.getClassificacaoComunicacao());
		processoComunicacao.getDocumentoList().add(documentoComunicacao);
		
		MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processoComunicacao);
		processoComunicacao.getMetadadoProcessoList().add(metadadoProcessoProvider
				.gerarMetadado(ComunicacaoMetadadoProvider.COMUNICACAO, documentoComunicacao.getId().toString()));
		
		for (DocumentoModeloComunicacao documentoModelo : modeloComunicacao.getDocumentos()) {
			Documento documento = documentoModelo.getDocumento();
			if (!documento.getDocumentoBin().equals(comunicacao)) {
				processoComunicacao.getDocumentoList().add(documentoManager.createDocumento(processoComunicacao, documento.getDescricao(), documento.getDocumentoBin(), documento.getClassificacaoDocumento()));
			}
		}
	}
	
	private MetadadoProcesso criarMetadadoDestinatario(DestinatarioModeloComunicacao destinatario, MetadadoProcessoProvider metadadoProcessoProvider) {
		if (destinatario.getDestinatario() != null) {
			PessoaFisica pessoaDestinatario = destinatario.getDestinatario();
			MetadadoProcesso metadadoRelator = destinatario.getModeloComunicacao().getProcesso().getMetadado(EppMetadadoProvider.RELATOR);
			if (metadadoRelator != null) {
				PessoaFisica relator = metadadoRelator.getValue();
				// Vai pra UDM do relator
				if (relator.equals(pessoaDestinatario)) {
					MetadadoProcesso metadadoUdm = destinatario.getModeloComunicacao().getProcesso().getMetadado(EppMetadadoProvider.UNIDADE_DECISORA_MONOCRATICA);
					UnidadeDecisoraMonocratica udmRelator = metadadoUdm.getValue();
					return metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.LOCALIZACAO_DESTINO, udmRelator.getLocalizacao().getIdLocalizacao().toString());
				}
			} else {
				return metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.PESSOA_DESTINATARIO, destinatario.getDestinatario().getIdPessoa().toString());
			}
		} else {
			return metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.LOCALIZACAO_DESTINO, destinatario.getDestino().getIdLocalizacao().toString());
		}
		return null;
	}
	
	public static final String MEIO_EXPEDICAO = "meioExpedicaoComunicacao"; 
	public static final String PRAZO_DESTINATARIO = "prazoDestinatarioComunicacao";
	public static final String NOME_DESTINATARIO = "nomeDestinatarioComunicacao";
}
