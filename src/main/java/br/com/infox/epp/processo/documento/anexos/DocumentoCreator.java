package br.com.infox.epp.processo.documento.anexos;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumentoBin;
import br.com.infox.epp.processo.entity.Processo;

abstract class DocumentoCreator {
    
    private Processo processo;
    private ProcessoDocumento processoDocumento;
    private List<ProcessoDocumento> documentosDaSessao;

    
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
    
    public List<ProcessoDocumento> getDocumentosDaSessao() {
        return documentosDaSessao;
    }

    public void setDocumentosDaSessao(List<ProcessoDocumento> documentosDaSessao) {
        this.documentosDaSessao = documentosDaSessao;
    }
    
    protected void newInstance() {
        setProcessoDocumento(new ProcessoDocumento());
        getProcessoDocumento().setProcessoDocumentoBin(new ProcessoDocumentoBin());
    }
    
    public void clear() {
        setDocumentosDaSessao(new ArrayList<ProcessoDocumento>());
        newInstance();
    }

}
