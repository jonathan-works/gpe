package br.com.infox.epp.processo.documento.anexos;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.DocumentoBinManager;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.infox.epp.processo.home.ProcessoHome;
import br.com.itx.util.Crypto;

@Name(DocumentoUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class DocumentoUploader implements FileUploadListener {

    public static final String NAME = "documentoUploader";
    
    @In private ProcessoDocumentoManager processoDocumentoManager;
    @In private DocumentoBinManager documentoBinManager;

    private ProcessoDocumento processoDocumento = new ProcessoDocumento();
    private List<ProcessoDocumento> documentosDaSessao = new ArrayList<>();

    public ProcessoDocumento getProcessoDocumento() {
        return processoDocumento;
    }

    public void setProcessoDocumento(ProcessoDocumento processoDocumento) {
        this.processoDocumento = processoDocumento;
    }

    public List<ProcessoDocumento> getDocumentosDaSessao() {
        return documentosDaSessao;
    }

    public void setDocumentosDaSessao(List<ProcessoDocumento> documentosDaSessao) {
        this.documentosDaSessao = documentosDaSessao;
    }

    @Override
    public void processFileUpload(FileUploadEvent fileUploadEvent) {
        final UploadedFile ui = fileUploadEvent.getUploadedFile();
        bin().setUsuario(Authenticator.getUsuarioLogado());
        bin().setNomeArquivo(ui.getName());
        bin().setExtensao(getFileType(ui.getName()));
        bin().setMd5Documento(getMD5(ui.getData()));
        bin().setSize(Long.valueOf(ui.getSize()).intValue());
        bin().setProcessoDocumento(ui.getData());
        bin().setModeloDocumento(null);
        FacesMessages.instance().add(Messages.instance().get("processoDocumento.doneLabel"));
    }
    
    private ProcessoDocumentoBin bin(){
        if (processoDocumento.getProcessoDocumentoBin() == null) {
            processoDocumento.setProcessoDocumentoBin(new ProcessoDocumentoBin());
        }
        return processoDocumento.getProcessoDocumentoBin();
    }
    
    private String getFileType(String nomeArquivo) {
        String ret = "";
        if (nomeArquivo != null) {
            ret = nomeArquivo.substring(nomeArquivo.lastIndexOf('.') + 1);
        }
        return ret;
    }
    
    private String getMD5(byte[] data) {
        return Crypto.encodeMD5(data);
    }
    
    public void persist() {
        try {
            processoDocumentoManager.gravarDocumentoNoProcesso(ProcessoHome.instance().getInstance(), processoDocumento);
            documentoBinManager.salvarBinario(processoDocumento.getIdProcessoDocumento(), bin().getProcessoDocumento());
        } catch (DAOException e) {
            e.printStackTrace();
        }
    }

}
