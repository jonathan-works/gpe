package br.com.infox.epp.processo.comunicacao.meioexpedicao;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class MeioExpedicaoList extends DataList<MeioExpedicao> {
    private static final long serialVersionUID = 1L;

    private String codigo;
    private String descricao;
    private Boolean eletronico;
    private Boolean ativo;
    
    @Override
    protected void addRestrictionFields() {
        addRestrictionField("codigo", RestrictionType.contendoLower);
        addRestrictionField("descricao", RestrictionType.contendoLower);
        addRestrictionField("eletronico", RestrictionType.igual);
        addRestrictionField("ativo", RestrictionType.igual);
    }
    
    @Override
    protected String getDefaultOrder() {
        return "descricao";
    }

    @Override
    protected String getDefaultEjbql() {
        return "select o from MeioExpedicao o";
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        Map<String, String> order = new HashMap<>();
        order.put("codigo", "codigo");
        order.put("descricao", "descricao");
        order.put("eletronico", "eletronico");
        order.put("ativo", "ativo");
        return order;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getEletronico() {
        return eletronico;
    }

    public void setEletronico(Boolean eletronico) {
        this.eletronico = eletronico;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    };
}
