package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.faces.FacesMessages;

import com.google.common.base.Strings;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DocumentoRespostaComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.list.RespostaComunicacaoList;
import br.com.infox.epp.processo.comunicacao.manager.ModeloComunicacaoManager;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.DocumentoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.PrazoComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.RespostaComunicacaoService;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.anexos.DocumentoEditor;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.assinatura.AssinaturaDocumentoService;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.service.DocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(RespostaComunicacaoAction.NAME)
@AutoCreate
@Scope(ScopeType.PAGE)
@Transactional
@ContextDependency
public class RespostaComunicacaoAction implements Serializable {
	
	public static final String NAME = "respostaComunicacaoAction";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(RespostaComunicacaoAction.class);
	
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	@In
	private RespostaComunicacaoList respostaComunicacaoList;
	@In
	private DocumentoDownloader documentoDownloader;
	@In
	private DocumentoManager documentoManager;
	@In
	private DocumentoComunicacaoList documentoComunicacaoList;
	@In
	private DocumentoService documentoService;
	@In
	private DocumentoComunicacaoService documentoComunicacaoService;
	@In
	private AssinaturaDocumentoService assinaturaDocumentoService;
	@In
	private RespostaComunicacaoService respostaComunicacaoService;
	@In
	private ModeloComunicacaoManager modeloComunicacaoManager;
	
	@Inject
	private PrazoComunicacaoService prazoComunicacaoService;
	@Inject
	private DocumentoUploader documentoUploader;
	@Inject
	private DocumentoEditor documentoEditor;
	
	private DestinatarioModeloComunicacao destinatario;
	private List<Documento> documentosComunicacao;
	private Boolean existeAnexoComunicacao;

	private Processo processoComunicacao;
	private Processo processoRaiz;
	private Date prazoResposta;
	private String statusProrrogacao;
	
	private List<ClassificacaoDocumento> classificacoesEditor;
	private List<ClassificacaoDocumento> classificacoesAnexo;
	private List<ModeloDocumento> modelosDocumento;
	
	private ModeloDocumento modeloDocumento;
	
	private boolean possivelMostrarBotaoEnvio = false;
	
	@Create
	public void init() {
		this.processoComunicacao = JbpmUtil.getProcesso();
		this.processoRaiz = processoComunicacao.getProcessoRoot();
		this.destinatario = processoComunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
		documentoUploader.newInstance();
		documentoUploader.clear();
		documentoUploader.setProcesso(processoRaiz);
		documentoEditor.setProcesso(processoRaiz);
		respostaComunicacaoList.setProcesso(processoComunicacao);
		documentoComunicacaoList.setProcesso(processoRaiz);
		documentoComunicacaoList.setModeloComunicacao(destinatario.getModeloComunicacao());
		newDocumentoEdicao();
		initClassificacoes();
		prazoResposta = prazoComunicacaoService.contabilizarPrazoCumprimento(processoComunicacao);
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
	
	public List<Documento> getDocumentosComunicacao(){
		if(documentosComunicacao == null){
			documentosComunicacao = modeloComunicacaoManager.getDocumentosByModeloComunicacao(destinatario.getModeloComunicacao());
			setExisteAnexoComunicacao(documentosComunicacao != null && !documentosComunicacao.isEmpty());
		}
		return documentosComunicacao;
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
			FacesMessages.instance().add("Registro gravado com sucesso");
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
			FacesMessages.instance().add("Registro gravado com sucesso");
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
				FacesMessages.instance().add("Resposta enviada com sucesso");
				modelosDocumento = null;
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
	
	public List<ModeloDocumento> getModelosDocumento() {
		if (modelosDocumento == null) {
			modelosDocumento = modeloDocumentoManager.getModeloDocumentoList();
		}
		return modelosDocumento;
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
	
	public Boolean getExisteAnexoComunicacao() {
		return existeAnexoComunicacao;
	}

	public void setExisteAnexoComunicacao(Boolean existeAnexoComunicacao) {
		this.existeAnexoComunicacao = existeAnexoComunicacao;
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
		}
		for (Documento documento : documentosResposta) {
			if(!assinaturaDocumentoService.isDocumentoTotalmenteAssinado(documento) || documento.getDocumentoBin().isMinuta()) {
				possivelMostrarBotaoEnvio = false;
				break;
			}
		}
	}
	
	private List<Documento> getDocumentoRespostaList(){
	    List<DocumentoRespostaComunicacao> documentosRespostaComunicacao = new ArrayList<DocumentoRespostaComunicacao>(respostaComunicacaoList.list());
	    List<Documento> documentosResposta = new ArrayList<Documento>();
	    for (DocumentoRespostaComunicacao documentoRespostaComunicacao : documentosRespostaComunicacao) {
            documentosResposta.add(documentoRespostaComunicacao.getDocumento());
        }
	    return documentosResposta;
	}
}
