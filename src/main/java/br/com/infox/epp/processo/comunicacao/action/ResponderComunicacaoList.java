package br.com.infox.epp.processo.comunicacao.action;

import java.util.Date;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.comunicacao.tipo.crud.TipoComunicacao;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;

@Named
@ViewScoped
public class ResponderComunicacaoList extends DataList<Processo> {

    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_JPQL = "select o from Processo o ";
    
    private static final String DEFAULT_WHERE = "where exists ( select 1 from MetadadoProcesso mp where mp.processo.idProcesso = o.idProcesso " +
                                                "                and mp.metadadoType = '" + EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType() + "' " +
                                                "                and mp.valor = '" + TipoProcesso.COMUNICACAO.value() + "' " + 
                                                "and o.dataFim is null and o.idJbpm is not null) ";
    
    private String numeroProcessoRoot;
    private String numeroProcesso;
    private TipoComunicacao tipoComunicacao;
    private Date dataCienciaFrom;
    private Date dataCienciaTo;
    private Date dataRespostaFrom;
    private Date dataRespostaTo;

    @Override
    protected String getDefaultOrder() {
        return "o.dataInicio";
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_JPQL;
    }
    
    @Override
    protected String getDefaultWhere() {
        return DEFAULT_WHERE;
    }

    public String getNumeroProcessoRoot() {
        return numeroProcessoRoot;
    }

    public void setNumeroProcessoRoot(String numeroProcessoRoot) {
        this.numeroProcessoRoot = numeroProcessoRoot;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public TipoComunicacao getTipoComunicacao() {
        return tipoComunicacao;
    }

    public void setTipoComunicacao(TipoComunicacao tipoComunicacao) {
        this.tipoComunicacao = tipoComunicacao;
    }

    public Date getDataCienciaFrom() {
        return dataCienciaFrom;
    }

    public void setDataCienciaFrom(Date dataCienciaFrom) {
        this.dataCienciaFrom = dataCienciaFrom;
    }

    public Date getDataCienciaTo() {
        return dataCienciaTo;
    }

    public void setDataCienciaTo(Date dataCienciaTo) {
        this.dataCienciaTo = dataCienciaTo;
    }

    public Date getDataRespostaFrom() {
        return dataRespostaFrom;
    }

    public void setDataRespostaFrom(Date dataRespostaFrom) {
        this.dataRespostaFrom = dataRespostaFrom;
    }

    public Date getDataRespostaTo() {
        return dataRespostaTo;
    }

    public void setDataRespostaTo(Date dataRespostaTo) {
        this.dataRespostaTo = dataRespostaTo;
    }

}
