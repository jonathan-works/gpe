package br.com.infox.epp.processo.documento.filter;

import java.util.List;

public class DocumentoFilter {

    private Integer idClassificacaoDocumento;
    private Integer numeroSequencialDocumento;
    private List<String> marcadores;

    public Integer getIdClassificacaoDocumento() {
        return idClassificacaoDocumento;
    }

    public void setIdClassificacaoDocumento(Integer idClassificacaoDocumento) {
        this.idClassificacaoDocumento = idClassificacaoDocumento;
    }

    public Integer getNumeroSequencialDocumento() {
        return numeroSequencialDocumento;
    }

    public void setNumeroSequencialDocumento(Integer numeroSequencialDocumento) {
        this.numeroSequencialDocumento = numeroSequencialDocumento;
    }

    public List<String> getMarcadores() {
        return marcadores;
    }

    public void setMarcadores(List<String> marcadores) {
        this.marcadores = marcadores;
    }
    
}