package br.com.infox.epp.processo.documento.anexos;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.DocumentoBin;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.log.LogProvider;

public abstract class DocumentoCreator {

    private Processo processo;
    private Documento documento;
    private List<Documento> documentosDaSessao;
    private Pasta pasta;

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo.getProcessoRoot();
    }

    public Documento getDocumento() {
        return documento;
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public List<Documento> getDocumentosDaSessao() {
        return documentosDaSessao;
    }

    public void setDocumentosDaSessao(List<Documento> documentosDaSessao) {
        this.documentosDaSessao = documentosDaSessao;
    }

    public void newInstance() {
        setDocumento(new Documento());
        getDocumento().setAnexo(true);
        getDocumento().setDocumentoBin(new DocumentoBin());
    }

    public void clear() {
        setDocumentosDaSessao(new ArrayList<Documento>());
        newInstance();
    }

    public void persist() {
        try {
            getDocumentosDaSessao().add(gravarDocumento());
        } catch (DAOException e) {
            getLogger().error("Não foi possível gravar o documento "
                    + getDocumento() + " no processo " + getProcesso(), e);
        }
        newInstance();
    }

    protected abstract LogProvider getLogger();

    protected abstract Documento gravarDocumento() throws DAOException;
    
    public Pasta getPasta() {
        return pasta;
    }

    public void setPasta(Pasta pasta) {
        this.pasta = pasta;
    }

}
