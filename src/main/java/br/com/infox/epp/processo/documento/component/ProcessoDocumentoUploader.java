package br.com.infox.epp.processo.documento.component;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.event.FileUploadEvent;
import org.richfaces.event.FileUploadListener;

import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.entity.Processo;

@Name(ProcessoDocumentoUploader.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProcessoDocumentoUploader implements FileUploadListener{

    public static final String NAME = "processoDocumentoUploader";
    
    private Processo processo;
    private ProcessoDocumento processoDocumento;
    private List<ProcessoDocumento> processoDocumentosDaSessao;

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public ProcessoDocumento getProcessoDocumento() {
        return processoDocumento;
    }

    public void setProcessoDocumento(ProcessoDocumento processoDocumento) {
        this.processoDocumento = processoDocumento;
    }

    public List<ProcessoDocumento> getProcessoDocumentosDaSessao() {
        return processoDocumentosDaSessao;
    }

    public void setProcessoDocumentosDaSessao(
            List<ProcessoDocumento> processoDocumentosDaSessao) {
        this.processoDocumentosDaSessao = processoDocumentosDaSessao;
    }

    @Override
    public void processFileUpload(FileUploadEvent arg0) {
        // TODO Auto-generated method stub
        
    }
    
    
    public void onClickTabAnexar(){
        setProcessoDocumento(new ProcessoDocumento());
        getProcessoDocumento().setProcessoDocumentoBin(new ProcessoDocumentoBin());
        
    }
    
    private void createProcessoDocumento(){
        processoDocumento.setProcesso(processo);
    }
    
    public void persist(){
        createProcessoDocumento();
    }
}
