package br.com.infox.epp.processo.comunicacao.action;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.file.download.FileDownloader;
import br.com.infox.core.file.encode.MD5Encoder;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.facade.ClassificacaoDocumentoFacade;
import br.com.infox.epp.documento.manager.ModeloDocumentoManager;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.service.ComunicacaoService;
import br.com.infox.epp.processo.documento.anexos.DocumentoUploader;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(ResponderComunicacaoAction.NAME)
@AutoCreate
@Scope(ScopeType.CONVERSATION)
public class ResponderComunicacaoAction implements Serializable {
	public static final String NAME = "responderComunicacaoAction";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(ResponderComunicacaoAction.class);
	
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
	private DocumentoManager documentoManager;
	
	private DestinatarioModeloComunicacao destinatario;
	private Processo processoComunicacao;
	
	private List<ClassificacaoDocumento> classificacoesEditor;
	private List<ClassificacaoDocumento> classificacoesAnexo;
	private List<ModeloDocumento> modelosDocumento;
	
	private ModeloDocumento modeloDocumento;
	private ClassificacaoDocumento classificacaoDocumento;
	private String textoResposta;
	private boolean minuta = false;
	
	private Documento documentoEdicao;
	
	@Create
	public void init() {
		this.processoComunicacao = JbpmUtil.getProcesso();
		this.destinatario = processoComunicacao.getMetadado(ComunicacaoService.DESTINATARIO).getValue();
		
		classificacoesEditor = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(true);
		if (classificacoesEditor != null && !classificacoesEditor.isEmpty() && classificacoesEditor.size() == 1) {
			classificacaoDocumento = classificacoesEditor.get(0);
		}
		
		classificacoesAnexo = classificacaoDocumentoFacade.getUseableClassificacaoDocumento(false);
		if (classificacoesAnexo != null && !classificacoesAnexo.isEmpty() && classificacoesAnexo.size() == 1) {
			setClassificacaoAnexo(classificacoesAnexo.get(0));
		}
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
			textoResposta = "";
		} else {
			textoResposta = modeloDocumentoManager.evaluateModeloDocumento(modeloDocumento);
		}
	}
	
	public void gravarResposta() {
		DocumentoBin bin = new DocumentoBin();
		bin.setMd5Documento(MD5Encoder.encode(textoResposta));
		bin.setModeloDocumento(textoResposta);
		try {
			documentoEdicao = documentoManager.createDocumento(processoComunicacao, "", bin, classificacaoDocumento);
		} catch (DAOException e) {
			LOG.error("", e);
			actionMessagesService.handleDAOException(e);
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
	
	public ClassificacaoDocumento getClassificacaoDocumento() {
		return classificacaoDocumento;
	}
	
	public void setClassificacaoDocumento(ClassificacaoDocumento classificacaoDocumento) {
		this.classificacaoDocumento = classificacaoDocumento;
	}
	
	public ClassificacaoDocumento getClassificacaoAnexo() {
		return documentoUploader.getClassificacaoDocumento();
	}
	
	public void setClassificacaoAnexo(ClassificacaoDocumento classificacaoDocumento) {
		documentoUploader.setClassificacaoDocumento(classificacaoDocumento);
	}
	
	public String getTextoResposta() {
		return textoResposta;
	}
	
	public void setTextoResposta(String textoResposta) {
		this.textoResposta = textoResposta;
	}
	
	public boolean isMinuta() {
		return minuta;
	}
	
	public void setMinuta(boolean minuta) {
		this.minuta = minuta;
	}
}
