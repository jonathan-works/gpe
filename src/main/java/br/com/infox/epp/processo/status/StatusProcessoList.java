package br.com.infox.epp.processo.status;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.status.entity.StatusProcesso;

@Named
@ViewScoped
public class StatusProcessoList extends DataList<StatusProcesso> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_JPQL = "select sp from StatusProcesso sp";
    private static final String DEFAULT_ORDER = "sp.descricao";
    
    private String nome;
    private String descricao;
    
    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_JPQL;
    }
    
    @Override
    protected void addRestrictionFields() {
        addRestrictionField("nome", "sp.nome", RestrictionType.contendoLower);
        addRestrictionField("descricao", "sp.descricao", RestrictionType.contendoLower);
    }
    
    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        Map<String, String> mapOrder = new HashMap<>();
        mapOrder.put("nome", "sp.nome");
        mapOrder.put("descricao", "sp.descricao");
        return mapOrder;
    }
    
    @Override
    public void newInstance() {
        this.setNome("");
        this.setDescricao("");
        super.newInstance();
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

}
