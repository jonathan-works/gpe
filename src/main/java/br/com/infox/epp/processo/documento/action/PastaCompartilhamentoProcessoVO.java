package br.com.infox.epp.processo.documento.action;

import java.util.ArrayList;
import java.util.List;

import br.com.infox.epp.processo.documento.entity.Pasta;

public class PastaCompartilhamentoProcessoVO {

    private String numeroProcesso;
    private List<Pasta> pastas;

    public PastaCompartilhamentoProcessoVO(String numeroProcesso, Pasta pasta) {
        this.numeroProcesso = numeroProcesso;
        this.pastas = new ArrayList<>();
        this.pastas.add(pasta);
    }

    public void addPasta(Pasta pasta) {
        pastas.add(pasta);
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public List<Pasta> getPastas() {
        return pastas;
    }

    public void setPastas(List<Pasta> pastas) {
        this.pastas = pastas;
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
