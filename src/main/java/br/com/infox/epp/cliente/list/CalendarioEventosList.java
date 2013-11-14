package br.com.infox.epp.cliente.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cliente.entity.CalendarioEventos;
import br.com.itx.util.ComponentUtil;

@Name(CalendarioEventosList.NAME)
@Scope(ScopeType.CONVERSATION)
public class CalendarioEventosList extends EntityList<CalendarioEventos> {
	
	public static final String NAME = "calendarioEventosList";

	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/CalendarioEventos/CalendarioEvTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "CalendarioEventos.xls";
	
	private static final String DEFAULT_EJBQL = "select o from CalendarioEventos o";
	private static final String DEFAULT_ORDER = "descricaoEvento";
	 
	@Override
	protected void addSearchFields() {
		addSearchField("descricaoEvento", SearchCriteria.CONTENDO);
		addSearchField("localizacao", SearchCriteria.IGUAL);
		addSearchField("dia", SearchCriteria.IGUAL);
		addSearchField("mes", SearchCriteria.IGUAL);
		addSearchField("ano", SearchCriteria.IGUAL);
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}
	
	public static CalendarioEventosList instance() {
		return ComponentUtil.getComponent(CalendarioEventosList.NAME);
	}
	
	@Override
    public EntityList<CalendarioEventos> getBeanList() {
        return CalendarioEventosList.instance();
    }

    @Override
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }   
    
	
}