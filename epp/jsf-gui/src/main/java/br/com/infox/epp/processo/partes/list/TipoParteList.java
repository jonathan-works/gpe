package br.com.infox.epp.processo.partes.list;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.DataList;
import br.com.infox.core.list.RestrictionType;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.processo.partes.entity.TipoParte;

@Named
@ViewScoped
public class TipoParteList extends DataList<TipoParte> {

    private static final long serialVersionUID = 1L;
    
    private static final String DEFAULT_JPQL = "select o from TipoParte o ";
    private static final String DEFAULT_ORDER = "o.descricao";
    
    private String identificador;
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
        addRestrictionField("identificador", RestrictionType.contendo);
        addRestrictionField("descricao", RestrictionType.contendo);
    }
    
    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        Map<String, String> orderMap = new HashMap<>(3);
        orderMap.put("identificador", "o.identificador");
        orderMap.put("descricao", "o.descricao");
        return orderMap;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
}
