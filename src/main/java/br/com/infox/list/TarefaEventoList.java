package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.TarefaEvento;

@Name(TarefaEventoList.NAME)
@Scope(ScopeType.PAGE)
public class TarefaEventoList extends EntityList<TarefaEvento> {
	
	public static final String NAME = "tarefaEventoList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from TarefaEvento o";
	private static final String DEFAULT_ORDER = "tarefa";
	
	private static final String R1 = "o.tarefa = #{tarefaEventoHome.tarefaAtual}";

	@Override
	protected void addSearchFields() {
		addSearchField("tarefa", SearchCriteria.igual, R1);
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
		// TODO Auto-generated method stub
		return null;
	}

}
