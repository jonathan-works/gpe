package br.com.infox.epp.list;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.util.ComponentUtil;

@Name(FluxoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class FluxoList extends EntityList<Fluxo> {
    private static final long serialVersionUID = 1L;
    private static final String DEFAULT_EJBQL = "select o from Fluxo o";
    private static final String DEFAULT_ORDER = "fluxo";
    
    public static final String NAME = "fluxoList";

    @Override
    protected void addSearchFields() {
        addSearchField("codFluxo", SearchCriteria.CONTENDO);
        addSearchField("fluxo", SearchCriteria.CONTENDO);
        addSearchField("publicado", SearchCriteria.IGUAL);
        addSearchField("ativo", SearchCriteria.IGUAL);
    }

    @Override
    protected String getDefaultEjbql() {
        return FluxoList.DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return FluxoList.DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }
    
    @Override
    public List<Fluxo> getResultList() {
		setEjbql(getEjbqlRestrictedWithDataPublicacao());
    	return super.getResultList();
    }

    public static final FluxoList instance() {
        return ComponentUtil.getComponent(FluxoList.NAME);
    }
    
    private String getEjbqlRestrictedWithDataPublicacao() {
    	if (getEntity().getDataInicioPublicacao() == null && getEntity().getDataFimPublicacao() == null) {
    		return getDefaultEjbql();
    	}
    	
    	StringBuilder sb = new StringBuilder(getDefaultEjbql());
    	sb.append(" where ");
    	
    	if (getEntity().getDataInicioPublicacao() != null && getEntity().getDataFimPublicacao() != null) {
    		sb.append("o.dataInicioPublicacao >= #{fluxoList.entity.dataInicioPublicacao}");
    		sb.append(" and o.dataFimPublicacao <= #{fluxoList.entity.dataFimPublicacao}");
    	} else if (getEntity().getDataInicioPublicacao() != null) {
    		sb.append("o.dataInicioPublicacao >= #{fluxoList.entity.dataInicioPublicacao}");
    	} else {
    		sb.append("o.dataFimPublicacao <= #{fluxoList.entity.dataFimPublicacao}");
    	}
    	
    	return sb.toString();
    }
}
