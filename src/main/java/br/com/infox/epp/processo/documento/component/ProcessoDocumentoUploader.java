package br.com.infox.epp.processo.documento.component;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.documento.manager.ProcessoDocumentoManager;
import br.com.itx.util.Crypto;

@Name(ProcessoDocumentoUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoUploader implements FileUploadListener {

    public static final String NAME = "processoDocumentoUploader";
    
    @In private ProcessoDocumentoManager processoDocumentoManager;

    private ProcessoDocumento processoDocumento = new ProcessoDocumento();

    public ProcessoDocumento getProcessoDocumento() {
        return processoDocumento;
    }

    public void setProcessoDocumento(ProcessoDocumento processoDocumento) {
        this.processoDocumento = processoDocumento;
    }

    @Override
    public void processFileUpload(FileUploadEvent fileUploadEvent) {
        final UploadedFile ui = fileUploadEvent.getUploadedFile();
        bin().setUsuario(Authenticator.getUsuarioLogado());
        bin().setNomeArquivo(ui.getName());
        bin().setExtensao(getFileType(ui.getName()));
        bin().setMd5Documento(getMD5(ui.getData()));
        bin().setSize(Long.valueOf(ui.getSize()).intValue());
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
        System.out.println(bin());
        System.out.println(bin().getDataInclusao());
        System.out.println(bin().getNomeArquivo());
        System.out.println(bin().getMd5Documento());
        System.out.println(bin().getProcessoDocumento());
        System.out.println(bin().getSize());
    }

}
