package br.com.infox.epp.processo.consulta.bean;

import java.io.Serializable;
import java.util.Date;

import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.Natureza;

public class ConsultaProcesso implements Serializable {

    private static final long serialVersionUID = 1L;

    private String numeroProcesso;
    private Date dataInicio;
    private Date dataFim;
    private Boolean inPesquisa = false;
    private String fluxo;
    private Natureza natureza;
    private Categoria categoria;

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getFluxo() {
        return fluxo;
    }

    public void setFluxo(String fluxo) {
        this.fluxo = fluxo;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = br.com.infox.core.util.DateUtil.getEndOfDay(dataFim);
    }

    public Boolean getInPesquisa() {
        return inPesquisa;
    }

    public void setInPesquisa(Boolean inPesquisa) {
        this.inPesquisa = inPesquisa;
    }

    @Override
    public String toString() {
        return numeroProcesso;
    }

    public void setNatureza(Natureza natureza) {
        this.natureza = natureza;
    }

    public Natureza getNatureza() {
        return natureza;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }

}
