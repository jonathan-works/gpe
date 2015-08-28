package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.faces.FacesMessages;

import com.google.common.base.Strings;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoRespostaComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.list.RespostaComunicacaoList;
import br.com.infox.epp.processo.comunicacao.service.DocumentoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.RespostaComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.anexos.DocumentoEditor;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Named
@Stateful
@ViewScoped
public class RespostaComunicacaoAction implements Serializable {
	
	public static final String NAME = "respostaComunicacaoAction";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(RespostaComunicacaoAction.class);
	
	private ActionMessagesService actionMessagesService = ComponentUtil.getComponent(ActionMessagesService.NAME);
	protected ModeloDocumentoManager modeloDocumentoManager = ComponentUtil.getComponent(ModeloDocumentoManager.NAME);
	private RespostaComunicacaoList respostaComunicacaoList = ComponentUtil.getComponent(RespostaComunicacaoList.NAME);
	private DocumentoManager documentoManager = ComponentUtil.getComponent(DocumentoManager.NAME);
	private DocumentoComunicacaoList documentoComunicacaoList = ComponentUtil.getComponent(DocumentoComunicacaoList.NAME);
	private DocumentoComunicacaoService documentoComunicacaoService = ComponentUtil.getComponent(DocumentoComunicacaoService.NAME);
	private AssinaturaDocumentoService assinaturaDocumentoService = ComponentUtil.getComponent(AssinaturaDocumentoService.NAME);
	private RespostaComunicacaoService respostaComunicacaoService = ComponentUtil.getComponent(RespostaComunicacaoService.NAME);
	
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	@Inject
	private DocumentoUploader documentoUploader;
	@Inject
	private DocumentoEditor documentoEditor;
	@Inject
	protected InfoxMessages infoxMessages;
	
	private DestinatarioModeloComunicacao destinatario;

	protected Processo processoComunicacao;
	private Processo processoRaiz;
	private Date prazoResposta;
	private String statusProrrogacao;
	
	private List<ClassificacaoDocumento> classificacoesEditor;
	private List<ClassificacaoDocumento> classificacoesAnexo;
	private List<ModeloDocumento> modeloDocumentoList;
	
	private ModeloDocumento modeloDocumento;
	
	private boolean possivelMostrarBotaoEnvio = false;
	
	
	@PostConstruct
	public void init() {
		this.processoComunicacao = JbpmUtil.getProcesso();
		respostaComunicacaoList.setProcesso(processoComunicacao);
		
		this.processoRaiz = processoComunicacao.getProcessoRoot();
		documentoUploader.newInstance();
		documentoUploader.clear();
		documentoUploader.setProcesso(processoRaiz);
		documentoEditor.setProcesso(processoRaiz);
		
		MetadadoProcesso metadadoDestinatario = processoComunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO);
		if(metadadoDestinatario != null){
			destinatario = metadadoDestinatario.getValue();
			documentoComunicacaoList.setModeloComunicacao(destinatario.getModeloComunicacao());
			prazoResposta = prazoComunicacaoService.getDataLimiteCumprimento(processoComunicacao);
		}

		newDocumentoEdicao();
		initClassificacoes();
		verificarPossibilidadeEnvioResposta();
	}

	public Long getIdDestinatario(){
		return destinatario.getId();
	}
	
	public DestinatarioModeloComunicacao getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(DestinatarioModeloComunicacao destinatario) {
		this.destinatario = destinatario;
	}
	
	public void assignModeloDocumento() {
		if (modeloDocumento == null) {
			documentoEditor.getDocumento().getDocumentoBin().setModeloDocumento("");
		} else {
			documentoEditor.getDocumento().getDocumentoBin().setModeloDocumento(modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento));
		}
	}
	
	public boolean isPossivelMostrarBotaoEnvio() {
		return possivelMostrarBotaoEnvio;
	}
	
	public void gravarResposta() {
		if (Strings.isNullOrEmpty(documentoEditor.getDocumento().getDocumentoBin().getModeloDocumento())) {
			FacesMessages.instance().add("Insira texto no editor");
			return;
		}
		try {
			if (!documentoManager.contains(documentoEditor.getDocumento())) {
				Documento documentoEdicao = getDocumentoEdicao();
				documentoEditor.persist();
				if (documentoEditor.getDocumentosDaSessao().isEmpty()) {
					return;
				}
				documentoComunicacaoService.vincularDocumentoRespostaComunicacao(documentoEdicao, processoComunicacao);
			} else {
				documentoManager.update(documentoEditor.getDocumento());
			}
			newDocumentoEdicao();
			FacesMessages.instance().add(infoxMessages.get("comunicacao.resposta.gravadoSucesso"));
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
		verificarPossibilidadeEnvioResposta();
	}
	
	public void newDocumentoEdicao() {
		documentoEditor.clear();
		documentoEditor.newInstance();
		documentoEditor.getDocumento().setPerfilTemplate(Authenticator.getUsuarioPerfilAtual().getPerfilTemplate());
		documentoEditor.getDocumento().setAnexo(false);
		modeloDocumento = null;
	}
	
	public void gravarAnexoResposta() {
		documentoUploader.persist();
		if (documentoUploader.getDocumentosDaSessao().isEmpty()) {
			return;
		}
		Documento resposta = documentoUploader.getDocumentosDaSessao().get(documentoUploader.getDocumentosDaSessao().size() - 1);
		try {
			documentoComunicacaoService.vincularDocumentoRespostaComunicacao(resposta, processoComunicacao);
			FacesMessages.instance().add(infoxMessages.get("comunicacao.resposta.gravadoSucesso"));
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
		documentoUploader.clear();
		verificarPossibilidadeEnvioResposta();
	}
	
	public void enviarRespostaComunicacao(){
		List<Documento> documentosRespostaComunicacao = getDocumentoRespostaList();
		try {
			if(!documentosRespostaComunicacao.isEmpty()){
			    long processId = BusinessProcess.instance().getProcessId();
			    long taskId = BusinessProcess.instance().getTaskId();
			    respostaComunicacaoService.enviarResposta(documentosRespostaComunicacao);
				BusinessProcess.instance().setProcessId(processId);
				BusinessProcess.instance().setTaskId(taskId);
				initClassificacoes();
				FacesMessages.instance().add(infoxMessages.get("comunicacao.resposta.enviadaSucesso"));
				modeloDocumentoList = null;
				newDocumentoEdicao();
				initClassificacoes();
			}
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
		verificarPossibilidadeEnvioResposta();
	}
	
	public void removerDocumento(Documento documento) {
		boolean isDocumentoEdicao = documentoEditor.getDocumento() != null && documentoEditor.getDocumento().equals(documento);
		try {
			documentoComunicacaoService.desvincularDocumentoRespostaComunicacao(documento);
			documentoManager.remove(documento);
			if (isDocumentoEdicao) {
				newDocumentoEdicao();
			}
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
		verificarPossibilidadeEnvioResposta();
	}
	
	public boolean podeRemoverDocumento(Documento documento) {
		return documento.getDocumentoBin().isMinuta() || !assinaturaDocumentoService.isDocumentoTotalmenteAssinado(documento);
	}

	public List<ClassificacaoDocumento> getClassificacoesEditor() {
		return classificacoesEditor;
	}
	
	public List<ClassificacaoDocumento> getClassificacoesAnexo() {
		return classificacoesAnexo;
	}
	
	public List<ModeloDocumento> getModeloDocumentoList() {
		if (modeloDocumentoList == null) {
			modeloDocumentoList = modeloDocumentoManager.getModeloDocumentoList();
		}
		return modeloDocumentoList;
	}
	
	public ModeloDocumento getModeloDocumento() {
		return modeloDocumento;
	}
	
	public void setModeloDocumento(ModeloDocumento modeloDocumento) {
		this.modeloDocumento = modeloDocumento;
	}
	
	public ClassificacaoDocumento getClassificacaoAnexo() {
		return documentoUploader.getClassificacaoDocumento();
	}

	public void setClassificacaoAnexo(ClassificacaoDocumento classificacaoDocumento) {
		documentoUploader.setClassificacaoDocumento(classificacaoDocumento);
	}
	
	public String getDescricaoAnexo() {
		return documentoUploader.getDocumento().getDescricao();
	}
	
	public void setDescricaoAnexo(String descricao) {
		documentoUploader.getDocumento().setDescricao(descricao);
	}
	
	public boolean isAnexoValido() {
		return documentoUploader.isValido();
	}
	
	public Documento getDocumentoEdicao() {
		return documentoEditor.getDocumento();
	}
	
	public void setDocumentoEdicao(Documento documentoEdicao) {
		documentoEditor.setDocumento(documentoEdicao);
	}
	
	public MeioExpedicao getMeioExpedicao() {
		return destinatario.getMeioExpedicao();
	}
	
	public TipoComunicacao getTipoComunicacao() {
		return destinatario.getModeloComunicacao().getTipoComunicacao();
	}

	public Date getPrazoResposta() {
		return prazoResposta;
	}

	public String getStatusProrrogacao() {
		setStatusProrrogacao(prazoComunicacaoService.getStatusProrrogacaoFormatado(processoComunicacao));
		if(Strings.isNullOrEmpty(statusProrrogacao)){
			if (prazoComunicacaoService.canTipoComunicacaoRequestProrrogacaoPrazo(destinatario.getModeloComunicacao().getTipoComunicacao())){
				setStatusProrrogacao("Não solicitada");
			}else{
				setStatusProrrogacao("Indisponível");
			}
		}
		return statusProrrogacao;
	}

	public void setStatusProrrogacao(String statusProrrogacao) {
		this.statusProrrogacao = statusProrrogacao;
	}

	private void initClassificacoes() {
		classificacoesEditor = documentoComunicacaoService.getClassificacoesDocumentoDisponiveisRespostaComunicacao(destinatario, true);
		classificacoesAnexo = documentoComunicacaoService.getClassificacoesDocumentoDisponiveisRespostaComunicacao(destinatario, false);
	}
	
	public void verificarPossibilidadeEnvioResposta() {
		possivelMostrarBotaoEnvio = true;
		List<Documento> documentosResposta = getDocumentoRespostaList();
		if (documentosResposta == null || documentosResposta.isEmpty()) {
			possivelMostrarBotaoEnvio = false;
			return;
		}
		for (Documento documento : documentosResposta) {
			if(!assinaturaDocumentoService.isDocumentoTotalmenteAssinado(documento) || documento.getDocumentoBin().isMinuta()) {
				possivelMostrarBotaoEnvio = false;
				return;
			}
		}
	}
	
	protected List<Documento> getDocumentoRespostaList(){
	    List<DocumentoRespostaComunicacao> documentosRespostaComunicacao = new ArrayList<DocumentoRespostaComunicacao>(respostaComunicacaoList.list());
	    List<Documento> documentosResposta = new ArrayList<Documento>();
	    for (DocumentoRespostaComunicacao documentoRespostaComunicacao : documentosRespostaComunicacao) {
            documentosResposta.add(documentoRespostaComunicacao.getDocumento());
        }
	    return documentosResposta;
	}
	
}
