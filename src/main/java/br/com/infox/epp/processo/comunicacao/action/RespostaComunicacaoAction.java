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
import br.com.infox.epp.documento.dao.ModeloDocumentoDAO;
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
import br.com.infox.seam.exception.BusinessException;

@Named
@Stateful
@ViewScoped
public class RespostaComunicacaoAction implements Serializable {
	
	public static final String NAME = "respostaComunicacaoAction";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(RespostaComunicacaoAction.class);
	
	@Inject
	private DocumentoComunicacaoService documentoComunicacaoService;
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	@Inject
	private DocumentoUploader documentoUploader;
	@Inject
	private DocumentoEditor documentoEditor;
	@Inject
	protected InfoxMessages infoxMessages;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private RespostaComunicacaoList respostaComunicacaoList;
	@Inject
	private DocumentoComunicacaoList documentoComunicacaoList;
	@Inject
	protected ModeloDocumentoManager modeloDocumentoManager;
	@Inject
	private DocumentoManager documentoManager;
	@Inject
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@Inject
	private RespostaComunicacaoService respostaComunicacaoService;
	@Inject
	private ModeloDocumentoDAO modeloDocumentoDAO;
	
	private DestinatarioModeloComunicacao destinatario;

	protected Processo processoComunicacao;
	protected Processo processoRaiz;
	protected Date prazoResposta;
	protected String statusProrrogacao;
	
	private List<ClassificacaoDocumento> classificacoesEditor;
	private List<ClassificacaoDocumento> classificacoesAnexo;
	private List<ModeloDocumento> modeloDocumentoList;
	
	private ModeloDocumento modeloDocumento;
	
	private boolean possivelMostrarBotaoEnvio = false;
	
	
	@PostConstruct
	public void init() {
		processoComunicacao = JbpmUtil.getProcesso();
		if (processoComunicacao != null) {
		    init(processoComunicacao);
		}
	}

    public void init(Processo processoComunicacao) {
        respostaComunicacaoList.setProcesso(processoComunicacao);
		prazoResposta = prazoComunicacaoService.getDataLimiteCumprimento(processoComunicacao);
		MetadadoProcesso metadadoDestinatario = processoComunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO);
		if(metadadoDestinatario != null){
			destinatario = metadadoDestinatario.getValue();
			documentoComunicacaoList.setModeloComunicacao(destinatario.getModeloComunicacao());
		}
		
		processoRaiz = processoComunicacao.getProcessoRoot();
		documentoUploader.newInstance();
		documentoUploader.clear();
		documentoUploader.setProcesso(processoRaiz);
		documentoEditor.setProcesso(processoRaiz);
		
		newDocumentoEdicao();
		initClassificacoes();
		verificarPossibilidadeEnvioResposta();
		
		documentoComunicacaoList.setModeloComunicacao(destinatario.getModeloComunicacao());
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
			respostaComunicacaoList.refresh();
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
		modeloDocumentoList = null;
	}
	
	//TODO ver como colocar esse método no service
	public void gravarAnexoResposta() {
		try {
			documentoUploader.persist();
		} catch (BusinessException e){
			LOG.error("", e);
			FacesMessages.instance().add(e.getMessage());
			return;
		}
		if (documentoUploader.getDocumentosDaSessao().isEmpty()) {
			return;
		}
		Documento resposta = documentoUploader.getDocumentosDaSessao().get(documentoUploader.getDocumentosDaSessao().size() - 1);
		try {
			documentoComunicacaoService.vincularDocumentoRespostaComunicacao(resposta, processoComunicacao);
			respostaComunicacaoList.refresh();
			FacesMessages.instance().add(infoxMessages.get("comunicacao.resposta.gravadoSucesso"));
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		} 
		documentoUploader.clear();
		verificarPossibilidadeEnvioResposta();
	}
	
	//TODO ver como colocar esse método no service
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
				respostaComunicacaoList.refresh();
			}
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
		verificarPossibilidadeEnvioResposta();
	}
	
	//TODO ver como colocar esse método no service
	public void removerDocumento(Documento documento) {
		boolean isDocumentoEdicao = documentoEditor.getDocumento() != null && documentoEditor.getDocumento().equals(documento);
		try {
			documentoComunicacaoService.desvincularDocumentoRespostaComunicacao(documento);
			documentoManager.remove(documento);
			if (isDocumentoEdicao) {
				newDocumentoEdicao();
			}
			respostaComunicacaoList.refresh();
			FacesMessages.instance().add("Documento removido com sucesso");
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
		if (modeloDocumentoList == null && getDocumentoEdicao() != null && getDocumentoEdicao().getClassificacaoDocumento() != null) {
			modeloDocumentoList = modeloDocumentoDAO.getModelosDocumentoLitsByClassificacaoEPapel(getDocumentoEdicao().getClassificacaoDocumento(), Authenticator.getPapelAtual());
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
	
	public ClassificacaoDocumento getClassificacaoDocumentoEditor() {
		return getDocumentoEdicao().getClassificacaoDocumento();
	}
	
	public void setClassificacaoDocumentoEditor(ClassificacaoDocumento classificacaoDocumento) {
		getDocumentoEdicao().setClassificacaoDocumento(classificacaoDocumento);
		modeloDocumentoList = null;
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
