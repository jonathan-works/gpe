package br.com.infox.epp.fluxo.xpdl.importer;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

@Name(XpdlUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class XpdlUploader implements FileUploadListener {
    
    public static final String NAME = "xpdlUploader";
    private byte[] data;

    @Override
    public void processFileUpload(FileUploadEvent event) {
        final UploadedFile ui = event.getUploadedFile();
        this.data = ui.getData();
    }
    
    public byte[] getData() {
        return data;
    }
}
