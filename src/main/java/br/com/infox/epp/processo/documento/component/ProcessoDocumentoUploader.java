package br.com.infox.epp.processo.documento.component;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;

@Name(ProcessoDocumentoUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoUploader implements FileUploadListener {
    
    public static final String NAME = "processoDocumentoUploader";

    @Override
    public void processFileUpload(FileUploadEvent arg0) {
        // TODO Auto-generated method stub

    }

}
