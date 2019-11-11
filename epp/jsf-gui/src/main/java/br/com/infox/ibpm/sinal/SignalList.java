package br.com.infox.ibpm.sinal;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class SignalList extends DataList<Signal> {

    private static final long serialVersionUID = 1L;
    
    private static final String  DEFAULT_JPQL = "select o from Signal o";
    private static final String  DEFAULT_ORDER = "o.nome";
    
    private String codigo;
    private String nome;
    private Boolean ativo; 
    
    @Override
    protected void addRestrictionFields() {
        addRestrictionField("codigo", RestrictionType.contendo);
        addRestrictionField("nome", RestrictionType.contendo);
        addRestrictionField("ativo", RestrictionType.igual);
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_JPQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
}
