package br.com.infox.epp.tarefa.caixa;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;

@Name(CaixaList.NAME)
@Scope(ScopeType.CONVERSATION)
public class CaixaList extends EntityList<Caixa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "caixaList";
	
	private static final String DEFAULT_EJBQL = "select o from Caixa o";
	private static final String DEFAULT_ORDER = "o.dsCaixa";
	
	private static final String R1 = "o.idNodeAnterior=#{caixaList.entity.idNodeAnterior} OR o.idNodeAnterior is null";

	@Override
	protected void addSearchFields() {
		addSearchField("tarefa", SearchCriteria.IGUAL);
		addSearchField("idNodeAnterior", SearchCriteria.IGUAL, R1);
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

}
