package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.seam.faces.FacesMessages;

import com.google.common.base.Strings;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.ModeloComunicacaoRascunhoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.RespostaComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.service.ProcessoAnaliseDocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.util.ComponentUtil;

@Named
@Stateful
@ViewScoped
public class ComunicacaoAction implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "comunicacaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(ComunicacaoAction.class);
	private static final String COMPROVANTE_DE_CIENCIA = "Comprovante de Ciência";
	
	private ModeloComunicacaoManager modeloComunicacaoManager = ComponentUtil.getComponent(ModeloComunicacaoManager.NAME);
	private ClassificacaoDocumentoFacade classificacaoDocumentoFacade = ComponentUtil.getComponent(ClassificacaoDocumentoFacade.NAME);
	private ProcessoAnaliseDocumentoService processoAnaliseDocumentoService = ComponentUtil.getComponent(ProcessoAnaliseDocumentoService.NAME);
	private DocumentoDownloader documentoDownloader = ComponentUtil.getComponent(DocumentoDownloader.NAME);
	private ModeloComunicacaoRascunhoList modeloComunicacaoRascunhoList = ComponentUtil.getComponent(ModeloComunicacaoRascunhoList.NAME);
	private RespostaComunicacaoService respostaComunicacaoService = ComponentUtil.getComponent(RespostaComunicacaoService.NAME);
	
	@Inject
	protected InfoxMessages infoxMessages;
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	@Inject
	private DocumentoUploader documentoUploader;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private EntityManager entityManager;
	
	private List<ModeloComunicacao> comunicacoes;
	private List<ClassificacaoDocumento> classificacoesDocumento;
	private List<ClassificacaoDocumento> classificacoesDocumentoProrrogacaoPrazo;
	private Processo processo;
	private List<Documento> documentosDestinatario; // Cache dos documentos do destinatário selecionado
	private Map<Long, Boolean> dadosCiencia = new HashMap<>(); // Cache das confirmações de ciência dos destinatários
	private List<DestinatarioBean> destinatarios;
	
	private DestinatarioBean destinatario;
	private Date dataCiencia;
	private boolean ciencia;
	private String textoCiencia;
	private boolean editorCiencia; 
	private ClassificacaoDocumento classificacaoDocumentoCiencia;
	
	private ClassificacaoDocumento classificacaoDocumentoProrrogPrazo;
	private boolean prorrogacaoPrazo;
	private boolean documentos;
	
	private boolean documentoResposta;
	private List<Documento> documentosListResposta;
	
	protected static final Comparator<DestinatarioBean> comparatorDestinatarios = new Comparator<DestinatarioBean>() {
		@Override
		public int compare(DestinatarioBean o1, DestinatarioBean o2) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				Date d1 = dateFormat.parse(o1.getDataEnvio());
				Date d2 = dateFormat.parse(o2.getDataEnvio());
				return d2.compareTo(d1);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	@PostConstruct
	public void init() {
		setProcesso(JbpmUtil.getProcesso());
	}
	
	public void setProcesso(Processo processo) {
	    clear();
	    this.processo = processo;
	    modeloComunicacaoRascunhoList.setProcesso(processo);
	}
	
	public List<ModeloComunicacao> getComunicacoesDoProcesso() {
		if (comunicacoes == null) {
			comunicacoes = modeloComunicacaoManager.listModelosComunicacaoPorProcessoRoot(processo.getNumeroProcessoRoot());
		}
		return comunicacoes;
	}
	
	public void clearCacheModelos() {
		this.comunicacoes = null;
		this.destinatario = null;
		this.destinatarios = null;
	}
	
	public List<DestinatarioBean> getDestinatarios() {
		if(destinatarios == null){
		    destinatarios = initDestinatarios();
		    Collections.sort(destinatarios, comparatorDestinatarios );
		}
	    return destinatarios;
	}

	protected List<DestinatarioBean> initDestinatarios() {
		List<DestinatarioBean> destinatarios = modeloComunicacaoManager.listDestinatarios(processo.getNumeroProcessoRoot());
		for (DestinatarioBean destinatario : destinatarios) {
			dadosCiencia.put(destinatario.getIdDestinatario(), !destinatario.getDataConfirmacao().equals("-"));
		}
		return destinatarios;
	}
	
	public List<ClassificacaoDocumento> getClassificacoesDocumento() {
		if (isCiencia()) {
			boolean isModelo = isEditorCiencia();
			classificacoesDocumento = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(isModelo);
		}
		return classificacoesDocumento;
	}
	
	public List<ClassificacaoDocumento> getClassificacoesDocumentoProrrogacaoPrazo() {
		if (classificacoesDocumentoProrrogacaoPrazo == null) {
			if (isProrrogacaoPrazo()) {
				classificacoesDocumentoProrrogacaoPrazo = new ArrayList<>();
				classificacoesDocumentoProrrogacaoPrazo.add(prazoComunicacaoService.getClassificacaoProrrogacaoPrazo(getDestinatarioModeloComunicacao(destinatario)));
			}
		}
		return classificacoesDocumentoProrrogacaoPrazo;
	}
	
	public ClassificacaoDocumento getClassificacaoDocumentoProrrogPrazo() {
		return classificacaoDocumentoProrrogPrazo;
	}

	public void setClassificacaoDocumentoProrrogPrazo(ClassificacaoDocumento classificacaoDocumentoProrrogPrazo) {
		this.classificacaoDocumentoProrrogPrazo = classificacaoDocumentoProrrogPrazo;
		documentoUploader.setClassificacaoDocumento(classificacaoDocumentoProrrogPrazo);
	}

	public DestinatarioBean getDestinatario() {
		return destinatario;
	}
	
	public void setDestinatarioCiencia(DestinatarioBean destinatario) {
		clear();
		this.destinatario = destinatario;
		ciencia = true;
//		try {
//			prazoComunicacaoService.darCiencia(getDestinatarioModeloComunicacao(destinatario).getProcesso(), new Date(), Authenticator.getUsuarioLogado());
//			clear();
//		} catch (DAOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public Date getDataCiencia() {
		return dataCiencia;
	}
	
	public void setDataCiencia(Date dataCiencia) {
		this.dataCiencia = dataCiencia;
	}
	
	public boolean isCiencia() {
		return ciencia;
	}
	
	public void darCiencia() {
		try {
			validarCiencia();
			Documento documento = criarDocumentoCiencia();
			prazoComunicacaoService.darCienciaManual(getDestinatarioModeloComunicacao(destinatario).getProcesso(), getDataCiencia(), documento);
			dadosCiencia.put(destinatario.getIdDestinatario(), true);
			clear();
			FacesMessages.instance().add(infoxMessages.get("comunicacao.msg.sucesso.ciencia"));
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		} catch (BusinessException e) {
			LOG.error("", e);
			FacesMessages.instance().add(e.getMessage());
		}
	}
	    
	private Documento criarDocumentoCiencia() {
		Documento documento = null;
		if (isEditorCiencia()) {	
			documento = new Documento();
			DocumentoBin bin = new DocumentoBin();
			documento.setDocumentoBin(bin);
			documento.setDescricao(COMPROVANTE_DE_CIENCIA);
			documento.setClassificacaoDocumento(getClassificacaoDocumentoCiencia());
			bin.setModeloDocumento(textoCiencia);
		} else {
			documento = documentoUploader.getDocumento();
			documento.setDescricao(documento.getDocumentoBin().getNomeArquivo());
			documento.setProcesso(getDestinatarioModeloComunicacao(destinatario).getProcesso().getProcessoRoot());
			documentoUploader.clear();
		}
		return documento;
	}

	private void validarCiencia() {
		StringBuilder msg = new StringBuilder();
		if (getClassificacaoDocumentoCiencia() == null) {
			msg.append(infoxMessages.get("comunicacao.msg.erro.classificacao"));
			msg.append("\n");
		}
		if (getDataCiencia() == null) {
			msg.append(infoxMessages.get("comunicacao.msg.erro.cienciaData"));
			msg.append("\n");
		}
		if (documentoUploader.getDocumento() == null && Strings.isNullOrEmpty(getTextoCiencia())) {
			msg.append(infoxMessages.get("comunicacao.msg.erro.cienciaDocumento"));
		}
		if (msg.length() > 0) {
			FacesMessages.instance().add(msg.toString());
			return;
		}
	}
	
	public boolean isProrrogacaoPrazo() {
		return prorrogacaoPrazo;
	}
	
	public void setDestinatarioProrrogacaoPrazo(DestinatarioBean destinatario) {
		clear();
		this.destinatario = destinatario;
		prorrogacaoPrazo = true;
		documentoUploader.setClassificacaoDocumento(null);
		classificacoesDocumentoProrrogacaoPrazo = null;
	}
	
	public boolean isDocumentos() {
		return documentos;
	}
	
	public void setDestinatarioDocumentos(DestinatarioBean destinatario) {
		clear();
		this.destinatario = destinatario;
		documentos = true;
	}
	
	public void pedirProrrogacaoPrazo() {
		try {
			Processo comunicacao = getDestinatarioModeloComunicacao(destinatario).getProcesso();
			Documento documento = documentoUploader.getDocumento();
			documento.setDescricao(documentoUploader.getClassificacaoDocumento().getDescricao());
			respostaComunicacaoService.enviarProrrogacaoPrazo(documento, comunicacao);
			documentoUploader.clear();
			clear();
			FacesMessages.instance().add(infoxMessages.get("comunicacao.msg.sucesso.pedidoProrrogacao"));
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		} catch (BusinessException e) {
			LOG.error("", e);
			FacesMessages.instance().add(e.getMessage());
		}
	}
	
	public Long getJbpmProcessId() {
		return JbpmUtil.getProcesso().getIdJbpm();
	}
	
	public List<Documento> getDocumentosDestinatario() {
		if (documentosDestinatario == null) {
			DestinatarioModeloComunicacao destinatarioModelo = getDestinatarioModeloComunicacao(destinatario);
			documentosDestinatario = new ArrayList<>();
			for (DocumentoModeloComunicacao documentoModelo : destinatarioModelo.getModeloComunicacao().getDocumentos()) {
				documentosDestinatario.add(documentoModelo.getDocumento());
				// FIXME: Só para não dar lazy na tela
				documentoModelo.getDocumento().getDocumentoBin().getSize();
			}
		}
		return documentosDestinatario;
	}

	protected DestinatarioModeloComunicacao getDestinatarioModeloComunicacao(DestinatarioBean bean) {
		return entityManager.find(DestinatarioModeloComunicacao.class, bean.getIdDestinatario());
	}
	
	public void downloadDocumento(Documento documento) {
		documentoDownloader.downloadDocumento(documento);
	}
	
	public void downloadComunicacao() {
		documentoDownloader.downloadDocumento(getDestinatarioModeloComunicacao(destinatario).getDocumentoComunicacao());
	}
	
	public boolean isCienciaConfirmada(DestinatarioBean bean) {
		return dadosCiencia.get(bean.getIdDestinatario());
	}
	
	public boolean podePedirProrrogacaoPrazo(DestinatarioBean bean) {
		DestinatarioModeloComunicacao destinatarioModeloComunicacao = getDestinatarioModeloComunicacao(bean);
	    return prazoComunicacaoService.canRequestProrrogacaoPrazo(destinatarioModeloComunicacao) && 
	                prazoComunicacaoService.getDataLimiteCumprimento(destinatarioModeloComunicacao.getProcesso()).after(new Date());
	}
	
	public void clear() {
		clearCacheModelos();
		ciencia = false;
		prorrogacaoPrazo = false;
		documentos = false;
		documentosDestinatario = null;
		destinatario = null;
		dataCiencia = null;
		documentoUploader.clear();		
		documentoResposta = false;
		documentosListResposta = null;
		setEditorCiencia(false);
		setClassificacaoDocumentoProrrogPrazo(null);
		
	}
	
	public String getTextoCiencia() {
		return textoCiencia;
	}

	public void setTextoCiencia(String textoCiencia) {
		this.textoCiencia = textoCiencia;
	}

	public boolean isEditorCiencia() {
		return editorCiencia;
	}

	public void setEditorCiencia(boolean editorCiencia) {
		this.editorCiencia = editorCiencia;
		if (!isEditorCiencia()){
			setClassificacaoDocumentoCiencia(null);
			setTextoCiencia(null);
		}
	}

	public ClassificacaoDocumento getClassificacaoDocumentoCiencia() {
		return classificacaoDocumentoCiencia;
	}

	public void setClassificacaoDocumentoCiencia(
			ClassificacaoDocumento classificacaoDocumentoCiencia) {
		this.classificacaoDocumentoCiencia = classificacaoDocumentoCiencia;
		if (!isEditorCiencia()){ 
			documentoUploader.setClassificacaoDocumento(classificacaoDocumentoCiencia);
		}
	}
	
	public List<Documento> getDocumentosRespostaList(){
		if (documentosListResposta == null) {
			if(destinatario != null){
				documentosListResposta = processoAnaliseDocumentoService.getDocumentosRespostaComunicacao(getDestinatarioModeloComunicacao(destinatario).getProcesso());
			}
		}
		return documentosListResposta;
	}
	
	public void setDestinatarioResposta(DestinatarioBean destinatario){
		clear();
		this.destinatario = destinatario;
		this.documentoResposta = true;
		this.documentosListResposta = null;
	}

	public boolean isDocumentoResposta() {
		return documentoResposta;
	}

	public void setDocumentoResposta(boolean documentoResposta) {
		this.documentoResposta = documentoResposta;
	}
	
	public String getComunicacoesExpedidasTitle(){
		return infoxMessages.get("comunicacao.comunicacoes");
	}
	
	public Date getStartDateCiencia(){
		if(destinatario != null){
			DestinatarioModeloComunicacao destinatarioModeloComunicacao = getDestinatarioModeloComunicacao(destinatario);
			return destinatarioModeloComunicacao.getProcesso().getDataInicio();
		}
		return null;
	}
	
	public Documento getComunicacaoDestinatario() {
		if (destinatario != null) {
			DestinatarioModeloComunicacao destinatarioModeloComunicacao = getDestinatarioModeloComunicacao(destinatario);
			return destinatarioModeloComunicacao.getDocumentoComunicacao();
		}
		return null;
	}
}
