package br.com.infox.epp.processo.documento.action;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.entity.Processo;

public class PastaCompartilhamentoProcessoVO {

    private Processo processo;
    private String numeroProcesso;
    private boolean documentoCompartilhado;
    private List<Pasta> pastas;
    private Long qtdDocumentoCompartilhado;

    public PastaCompartilhamentoProcessoVO(Processo processo, Pasta pasta) {
        this.setProcesso(processo);
        this.numeroProcesso = processo.getNumeroProcessoRoot();
        this.documentoCompartilhado = false;
        this.pastas = new ArrayList<>(1);
        this.pastas.add(pasta);
        this.qtdDocumentoCompartilhado = null;
    }

    public PastaCompartilhamentoProcessoVO(Processo processo, boolean documentoCompartilhado) {
        this.setProcesso(processo);
        this.numeroProcesso = processo.getNumeroProcessoRoot();
        this.documentoCompartilhado = documentoCompartilhado;
        this.pastas = new ArrayList<>(0);
        this.qtdDocumentoCompartilhado = null;
    }

    public void addPasta(Pasta pasta) {
        pastas.add(pasta);
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public boolean isDocumentoCompartilhado() {
        return documentoCompartilhado;
    }

    public void setDocumentoCompartilhado(boolean documentoCompartilhado) {
        this.documentoCompartilhado = documentoCompartilhado;
    }

    public List<Pasta> getPastas() {
        return pastas;
    }

    public void setPastas(List<Pasta> pastas) {
        this.pastas = pastas;
    }

    public Long getQtdDocumentoCompartilhado() {
        return qtdDocumentoCompartilhado;
    }

    public void setQtdDocumentoCompartilhado(Long qtdDocumentoCompartilhado) {
        this.qtdDocumentoCompartilhado = qtdDocumentoCompartilhado;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((numeroProcesso == null) ? 0 : numeroProcesso.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PastaCompartilhamentoProcessoVO other = (PastaCompartilhamentoProcessoVO) obj;
        if (numeroProcesso == null) {
            if (other.numeroProcesso != null)
                return false;
        } else if (!numeroProcesso.equals(other.numeroProcesso))
            return false;
        return true;
    }
}
