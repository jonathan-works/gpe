package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.bpm.TaskInstance;
import org.jboss.seam.faces.FacesMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.MeioExpedicao;
import br.com.infox.epp.processo.comunicacao.list.DocumentoComunicacaoList;
import br.com.infox.epp.processo.comunicacao.list.RespostaComunicacaoList;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.RespostaComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoDownloader;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.documento.service.DocumentoService;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.ibpm.task.home.TaskInstanceHome;
import br.com.infox.ibpm.util.JbpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(RespostaComunicacaoAction.NAME)
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class RespostaComunicacaoAction implements Serializable {
	public static final String NAME = "respostaComunicacaoAction";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(RespostaComunicacaoAction.class);
	
	@In
	private ComunicacaoService comunicacaoService;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private ClassificacaoDocumentoFacade classificacaoDocumentoFacade;
	@In
	private ModeloDocumentoManager modeloDocumentoManager;
	@In
	private DocumentoUploader documentoUploader;
	@In
	private RespostaComunicacaoService respostaComunicacaoService;
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
	
	private DestinatarioModeloComunicacao destinatario;
	private Processo processoComunicacao;
	private Processo processoResposta;
	private Date prazoResposta;
	
	private List<ClassificacaoDocumento> classificacoesEditor;
	private List<ClassificacaoDocumento> classificacoesAnexo;
	private List<ModeloDocumento> modelosDocumento;
	
	private ModeloDocumento modeloDocumento;
	
	private Documento documentoEdicao;
	
	@Create
	public void init() {
		this.processoComunicacao = JbpmUtil.getProcesso();
		this.destinatario = processoComunicacao.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
		criarProcessoResposta();
		documentoUploader.newInstance();
		documentoUploader.clear();
		documentoUploader.setProcesso(processoResposta);
		respostaComunicacaoList.setProcessoResposta(processoResposta);
		documentoComunicacaoList.setProcessoComunicacao(processoComunicacao);
		documentoComunicacaoList.setModeloComunicacao(destinatario.getModeloComunicacao());
		newDocumentoEdicao();
		initClassificacoes();
		prazoResposta = comunicacaoService.contabilizarPrazoCumprimento(processoComunicacao);
	}

	public void downloadComunicacao() {
		try {
			byte[] pdf = comunicacaoService.gerarPdfCompleto(destinatario.getModeloComunicacao(), destinatario);
			FileDownloader.download(pdf, "application/pdf", "Comunicação.pdf");
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
	
	public void downloadDocumento(Documento documento) {
		documentoDownloader.downloadDocumento(documento);
	}
	
	public void assignModeloDocumento() {
		if (modeloDocumento == null) {
			documentoEdicao.getDocumentoBin().setModeloDocumento("");
		} else {
			documentoEdicao.getDocumentoBin().setModeloDocumento(modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento));
		}
	}
	
	public void gravarResposta() {
		boolean hasId = documentoEdicao.getId() != null;
		try {
			String textoEditor = documentoEdicao.getDocumentoBin().getModeloDocumento();
			if (textoEditor == null || textoEditor.isEmpty()) {
				FacesMessages.instance().add("Insira texto no editor");
				return;
			}
			documentoEdicao = respostaComunicacaoService.gravarDocumentoResposta(documentoEdicao, processoResposta);
			processoResposta.getDocumentoList().add(documentoEdicao);
			FacesMessages.instance().add("Registro gravado com sucesso");
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
			if (!hasId) {
				documentoEdicao.setId(null);
				documentoEdicao.getDocumentoBin().setId(null);
			}
		}
	}
	
	public void newDocumentoEdicao() {
		documentoEdicao = new Documento();
		DocumentoBin bin = new DocumentoBin();
		documentoEdicao.setDocumentoBin(bin);
		documentoEdicao.setPerfilTemplate(Authenticator.getUsuarioPerfilAtual().getPerfilTemplate());
		modeloDocumento = null;
	}
	
	public void gravarAnexoResposta() {
		processoResposta.getDocumentoList().add(documentoUploader.getDocumento());
		documentoUploader.persist();
		documentoUploader.clear();
	}
	
	public void removerDocumento(Documento documento) {
		boolean isDocumentoEdicao = documentoEdicao != null && documentoEdicao.equals(documento);
		try {
			documentoService.removerDocumento(documento);
			processoResposta.getDocumentoList().remove(documento);
			if (isDocumentoEdicao) {
				newDocumentoEdicao();
			}
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
		}
	}
	
	public void endTask() {
		try {
			long taskProcessoComunicacaoId = TaskInstance.instance().getId();
			respostaComunicacaoService.inicializarFluxoDocumento(processoResposta);
			JbpmUtil.getJbpmSession().flush();
			TaskInstanceHome taskInstanceHome = TaskInstanceHome.instance();
			taskInstanceHome.setTaskId(taskProcessoComunicacaoId);
			taskInstanceHome.setCurrentTaskInstance(ManagedJbpmContext.instance().getTaskInstanceForUpdate(taskProcessoComunicacaoId));
			taskInstanceHome.end(taskInstanceHome.getName());
		} catch (Exception e) {
			LOG.error("", e);
			if (e instanceof DAOException) {
				actionMessagesService.handleDAOException((DAOException) e);
			} else {
				actionMessagesService.handleException("Erro ao enviar resposta", e);
			}
		}
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
		return documentoEdicao;
	}
	
	public void setDocumentoEdicao(Documento documentoEdicao) {
		this.documentoEdicao = documentoEdicao;
	}
	
	public boolean isPossuiResposta() {
		return !processoResposta.getDocumentoList().isEmpty();
	}
	
	public boolean isPossuiProcessoResposta() {
		return processoResposta != null;
	}
	
	public MeioExpedicao getMeioExpedicao() {
		return destinatario.getMeioExpedicao();
	}

	public Date getPrazoResposta() {
		return prazoResposta;
	}
	
	private void initClassificacoes() {
		classificacoesEditor = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(true);
		classificacoesAnexo = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(false);
	}

	private void criarProcessoResposta() {
		MetadadoProcesso metadado = processoComunicacao.getMetadado(ComunicacaoMetadadoProvider.RESPOSTA_COMUNICACAO_ATUAL);
		if (metadado != null) {
			processoResposta = metadado.getValue();
		} else {
			try {
				processoResposta = respostaComunicacaoService.criarProcessoResposta(processoComunicacao);
			} catch (DAOException e) {
				LOG.error("", e);
				actionMessagesService.handleDAOException(e);
			}
		}
	}
}
