package br.com.infox.epp.tarefa.estatistica.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.tarefa.estatistica.entity.TempoMedioTarefa;
import br.com.itx.util.ComponentUtil;

@Name(TempoMedioTarefaList.NAME)
@BypassInterceptors
@Scope(ScopeType.CONVERSATION)
public class TempoMedioTarefaList extends EntityList<TempoMedioTarefa> {
	public static final String NAME = "tempoMedioTarefaList";
	
	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/Estatistica/tempoMedioTarefaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "temposMediosTarefa.xls";

	private static final String DEFAULT_EJBQL = "select o from TempoMedioTarefa o";
	private static final String DEFAULT_ORDER = "idTarefa";
	
	@Override
	protected void addSearchFields() {
		addSearchField("tempoMedioProcesso", SearchCriteria.IGUAL);
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
	
	 @Override
    public EntityList<TempoMedioTarefa> getBeanList() {
        return ComponentUtil.getComponent(TempoMedioTarefaList.NAME);
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
