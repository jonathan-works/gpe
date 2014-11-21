package br.com.infox.epp.processo.comunicacao.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.joda.time.DateTime;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.core.pdf.PdfManager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.documento.type.ArbitraryExpressionResolver;
import br.com.infox.epp.documento.type.ExpressionResolverChain;
import br.com.infox.epp.documento.type.JbpmExpressionResolver;
import br.com.infox.epp.documento.type.SeamExpressionResolver;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.NaturezaCategoriaFluxoManager;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
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
import br.com.infox.epp.processo.metadado.type.MetadadoProcessoType;
import br.com.infox.epp.processo.service.IniciarProcessoService;
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
	
	public void expedirComunicacao(DestinatarioModeloComunicacao destinatario) throws DAOException {
		ModeloComunicacao modeloComunicacao = destinatario.getModeloComunicacao();
		Localizacao localizacao = Authenticator.getLocalizacaoAtual();
		Fluxo fluxo = fluxoManager.getFluxoByCodigo(codigoFluxoComunicacao);
		if (fluxo == null) {
			throw new DAOException("Fluxo de comunicação não encontrado");
		}
		List<NaturezaCategoriaFluxo> ncfs = naturezaCategoriaFluxoManager.getActiveNaturezaCategoriaFluxoListByFluxo(fluxo);
		if (ncfs.isEmpty()) {
			throw new DAOException("Não existe natureza/categoria/fluxo configurada para o fluxo de comunicação");
		}
		NaturezaCategoriaFluxo ncf = ncfs.get(0);
		
		Processo processo = new Processo();
		processo.setLocalizacao(localizacao);
		processo.setNaturezaCategoriaFluxo(ncf);
		
		processo.getMetadadoProcessoList().addAll(criarMetadados(destinatario, processo));
		processo.setNumeroProcesso("");
		processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
		processoManager.persist(processo);
		
		DocumentoBin comunicacao = destinatario.getComunicacao();
		Documento documentoComunicacao = documentoManager.createDocumento(processo, comunicacao.getNomeArquivo(), comunicacao, modeloComunicacao.getClassificacaoComunicacao());
		processo.getDocumentoList().add(documentoComunicacao);
		processo.getMetadadoProcessoList().add(criarMetadado(COMUNICACAO, Documento.class, documentoComunicacao.getId().toString(), processo));
		
		for (DocumentoModeloComunicacao documentoModelo : modeloComunicacao.getDocumentos()) {
			Documento documento = documentoModelo.getDocumento();
			processo.getDocumentoList().add(documentoManager.createDocumento(processo, documento.getDescricao(), documento.getDocumentoBin(), documento.getClassificacaoDocumento()));
		}
		
		iniciarProcessoService.iniciarProcesso(processo);
		destinatario.setExpedido(true);
		genericManager.update(destinatario);
	}
	
	public byte[] gerarPdfCompleto(ModeloComunicacao modeloComunicacao, DestinatarioModeloComunicacao destinatario) throws DAOException {
		ByteArrayOutputStream pdf = new ByteArrayOutputStream();
		try {
			ByteArrayOutputStream pdfComunicacao = new ByteArrayOutputStream();
			if (destinatario == null) {
				pdfManager.convertHtmlToPdf(modeloComunicacao.getComunicacao(), pdfComunicacao);
			} else {
				pdfManager.convertHtmlToPdf(evaluateComunicacao(destinatario), pdfComunicacao);
			}
			
			com.lowagie.text.Document pdfDocument = new com.lowagie.text.Document();
			PdfCopy copy = new PdfCopy(pdfDocument, pdf);
			pdfDocument.open();
			
			copy = pdfManager.copyPdf(copy, pdfComunicacao.toByteArray());
			pdfComunicacao.reset();
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
	

	public void finalizarComunicacao(ModeloComunicacao modeloComunicacao) throws DAOException {
		for (DestinatarioModeloComunicacao destinatario : modeloComunicacao.getDestinatarios()) {
			DocumentoBin comunicacao = documentoBinManager.createProcessoDocumentoBin("Comunicação", modeloComunicacao.getComunicacao());
			destinatario.setComunicacao(comunicacao);
		}
		modeloComunicacaoManager.update(modeloComunicacao);
	}
	
	public String evaluateComunicacao(DestinatarioModeloComunicacao destinatario) {
		Map<String, String> variaveis = createVariaveis(destinatario);
		ArbitraryExpressionResolver arbitraryExpressionResolver = new ArbitraryExpressionResolver(variaveis);
		
		ProcessInstance processInstance = jbpmContext.getProcessInstance(destinatario.getModeloComunicacao().getProcesso().getIdJbpm());
		variableTypeResolver.setProcessInstance(processInstance);
		JbpmExpressionResolver jbpmExpressionResolver = new JbpmExpressionResolver(variableTypeResolver.getVariableTypeMap(), processInstance.getContextInstance());
		
		SeamExpressionResolver seamExpressionResolver = new SeamExpressionResolver();
		
		ExpressionResolverChain chain = new ExpressionResolverChain(arbitraryExpressionResolver, jbpmExpressionResolver, seamExpressionResolver);
		DocumentoBin comunicacao = destinatario.getComunicacao();
		ModeloDocumento modeloDocumento = destinatario.getModeloComunicacao().getModeloDocumento();
		return modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento, comunicacao.getModeloDocumento(), chain);
	}
	
	private MetadadoProcesso criarMetadado(String tipo, Class<?> classType, String valor, Processo processo) {
		MetadadoProcesso metadadoProcesso = new MetadadoProcesso();
		metadadoProcesso.setClassType(classType);
		metadadoProcesso.setMetadadoType(tipo);
		metadadoProcesso.setValor(valor);
		metadadoProcesso.setProcesso(processo);
		return metadadoProcesso;
	}
	
	private Collection<MetadadoProcesso> criarMetadados(DestinatarioModeloComunicacao destinatario, Processo processo) {
		Collection<MetadadoProcesso> metadados = new ArrayList<>();
		ModeloComunicacao modeloComunicacao = destinatario.getModeloComunicacao();
		DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		
		// Destinatário / Destino
		if (destinatario.getDestinatario() != null) {
			metadados.add(criarMetadado(MetadadoProcessoType.PESSOA_DESTINATARIO, PessoaFisica.class, destinatario.getDestinatario().getIdPessoa().toString(), processo));
		} else {
			metadados.add(criarMetadado(MetadadoProcessoType.LOCALIZACAO_DESTINO, Localizacao.class, destinatario.getDestino().getIdLocalizacao().toString(), processo));
		}
		
		Date dataFimPrazoCiencia = DateTime.now().plusDays(modeloComunicacao.getTipoComunicacao().getQuantidadeDiasCiencia()).toDate();
		metadados.add(criarMetadado(DATA_FIM_PRAZO_CIENCIA, Date.class, dateFormatter.format(dataFimPrazoCiencia), processo));
		
		metadados.add(criarMetadado(MEIO_EXPEDICAO, MeioExpedicao.class, destinatario.getMeioExpedicao().getLabel(), processo));
		
		metadados.add(criarMetadado(TIPO_COMUNICACAO, TipoComunicacao.class, modeloComunicacao.getTipoComunicacao().getId().toString(), processo));
		
		metadados.add(criarMetadado(MODELO_COMUNICACAO, ModeloComunicacao.class, modeloComunicacao.getId().toString(), processo));
		
		if (destinatario.getPrazo() != null) {
			metadados.add(criarMetadado(PRAZO_DESTINATARIO, Integer.class, destinatario.getPrazo().toString(), processo));
		}
		
		metadados.add(criarMetadado(MetadadoProcessoType.TIPO_PROCESSO, String.class, TIPO_PROCESSO_COMUNICACAO, processo));
		
		return metadados;
	}
	
	private Map<String, String> createVariaveis(DestinatarioModeloComunicacao destinatario) {
		DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		ModeloComunicacao modeloComunicacao = destinatario.getModeloComunicacao();
		
		Map<String, String> variaveis = new HashMap<>();
		String format = "#'{'{0}'}'";

		Date dataFimPrazoCiencia = DateTime.now().plusDays(modeloComunicacao.getTipoComunicacao().getQuantidadeDiasCiencia()).toDate();
		variaveis.put(MessageFormat.format(format, DATA_FIM_PRAZO_CIENCIA), dateFormatter.format(dataFimPrazoCiencia));
		
		variaveis.put(MessageFormat.format(format, MEIO_EXPEDICAO), destinatario.getMeioExpedicao().getLabel());
		variaveis.put(MessageFormat.format(format, MODELO_COMUNICACAO), modeloComunicacao.getId().toString());
		variaveis.put(MessageFormat.format(format, PRAZO_DESTINATARIO), destinatario.getPrazo() != null ? destinatario.getPrazo().toString() : null);
		variaveis.put(MessageFormat.format(format, TIPO_COMUNICACAO), modeloComunicacao.getTipoComunicacao().getId().toString());
		variaveis.put(MessageFormat.format(format, "nomeDestinatario"), destinatario.getNome());
		return variaveis;
	}
	
	// Metadados
	public static final String DATA_FIM_PRAZO_CIENCIA = "dataFimPrazoCiencia";
	public static final String MEIO_EXPEDICAO = "meioExpedicao";
	public static final String TIPO_COMUNICACAO = "tipoComunicacao";
	public static final String MODELO_COMUNICACAO = "modeloComunicacao";
	public static final String PRAZO_DESTINATARIO = "prazoDestinatario";
	public static final String COMUNICACAO = "comunicacao";
	
	public static final String TIPO_PROCESSO_COMUNICACAO = "Comunicação";
}
