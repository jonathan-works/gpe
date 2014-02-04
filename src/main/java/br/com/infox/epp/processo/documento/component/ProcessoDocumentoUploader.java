package br.com.infox.epp.processo.documento.component;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.Messages;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;
import org.richfaces.model.UploadedFile;

import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.entity.Processo;

@Name(ProcessoDocumentoUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoUploader implements FileUploadListener {

    public static final String NAME = "processoDocumentoUploader";

    private byte[] data;
    private String fileName;
    private Integer size;
    private String contentType;
    private Processo processo;
    private ProcessoDocumento processoDocumento;

    public ProcessoDocumento getProcessoDocumento() {
        return processoDocumento;
    }

    public void setProcessoDocumento(ProcessoDocumento processoDocumento) {
        this.processoDocumento = processoDocumento;
    }

    @Override
    public void processFileUpload(FileUploadEvent fileUploadEvent) {
        final UploadedFile ui = fileUploadEvent.getUploadedFile();
        this.data = ui.getData();
        this.fileName = ui.getName();
        this.size = Long.valueOf(ui.getSize()).intValue();
        this.contentType = ui.getContentType();
        FacesMessages.instance().add(Messages.instance().get("processoDocumento.doneLabel"));
    }

}
