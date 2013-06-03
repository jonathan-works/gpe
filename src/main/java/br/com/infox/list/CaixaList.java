package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Caixa;

@Name(CaixaList.NAME)
@Scope(ScopeType.EVENT)
public class CaixaList extends EntityList<Caixa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "caixaList";
	
	private static final String DEFAULT_EJBQL = "select o from Caixa o";
	private static final String DEFAULT_ORDER = "o.caixa";
	
	public static final String R1 = "o.tarefa = #{caixaList.entity.tarefa}";
	public static final String R2 = "o.tarefaAnterior = #{caixaList.entity.tarefaAnterior} OR o.tarefaAnterior is null";

	@Override
	protected void addSearchFields() {
		addSearchField("tarefa", SearchCriteria.igual, R1);
		addSearchField("tarefaAnterior", SearchCriteria.igual, R2);
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
