package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.list.RespostaComunicacaoList;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.comunicacao.service.RespostaComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;

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
	
	private DestinatarioModeloComunicacao destinatario;
	private Processo processoComunicacao;
	
	private List<ClassificacaoDocumento> classificacoesEditor;
	private List<ClassificacaoDocumento> classificacoesAnexo;
	private List<ModeloDocumento> modelosDocumento;
	
	private ModeloDocumento modeloDocumento;
	private boolean minuta = false;
	
	private Documento documentoEdicao;
	
	@Create
	public void init() {
		this.processoComunicacao = JbpmUtil.getProcesso();
		this.destinatario = processoComunicacao.getMetadado(ComunicacaoService.DESTINATARIO).getValue();
		respostaComunicacaoList.setProcessoComunicacao(processoComunicacao);
		newDocumentoEdicao();
		initClassificacoes();
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
			documentoEdicao = respostaComunicacaoService.gravarResposta(documentoEdicao, processoComunicacao);
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
	
	public void gravarAnexoResposta() {
		documentoUploader.persist();
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
	
	public Documento getDocumentoEdicao() {
		return documentoEdicao;
	}
	
	public void setDocumentoEdicao(Documento documentoEdicao) {
		this.documentoEdicao = documentoEdicao;
		if (documentoEdicao != null) {
			minuta = documentoEdicao.getDocumentoBin().isMinuta();
		} else {
			minuta = false;
		}
	}
	
	public boolean isMinuta() {
		return minuta;
	}
	
	public void setMinuta(boolean minuta) {
		this.minuta = minuta;
	}
	
	private void initClassificacoes() {
		classificacoesEditor = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(true);
		if (classificacoesEditor != null && !classificacoesEditor.isEmpty() && classificacoesEditor.size() == 1) {
			documentoEdicao.setClassificacaoDocumento(classificacoesEditor.get(0));
		}
		
		classificacoesAnexo = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(false);
		if (classificacoesAnexo != null && !classificacoesAnexo.isEmpty() && classificacoesAnexo.size() == 1) {
			setClassificacaoAnexo(classificacoesAnexo.get(0));
		}
	}
	
	private void newDocumentoEdicao() {
		documentoEdicao = new Documento();
		DocumentoBin bin = new DocumentoBin();
		documentoEdicao.setDocumentoBin(bin);
		documentoEdicao.setPerfilTemplate(Authenticator.getUsuarioPerfilAtual().getPerfilTemplate());
	}
}
