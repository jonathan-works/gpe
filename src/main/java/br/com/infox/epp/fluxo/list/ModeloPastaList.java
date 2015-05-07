package br.com.infox.epp.fluxo.list;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.ModeloPasta;

@Name(ModeloPastaList.NAME)
@Scope(ScopeType.PAGE) //TODO avaliar se é suficiente
public class ModeloPastaList extends EntityList<ModeloPasta>{

	private static final long serialVersionUID = 1L;
	static final String NAME = "modeloPastaList";
	
	private final String DEFAULT_EJBQL = "select o from ModeloPasta o";
	private final String DEFAULT_ORDER = "ordem";

	
	@Override
	protected void addSearchFields() {
		addSearchField("nome", SearchCriteria.CONTENDO);
		addSearchField("fluxo", SearchCriteria.IGUAL);
	}

	//TODO ver se precisa testar quando o fluxo não existe
	@Override
	public Long getResultCount(){
//		return 0L;
		return super.getResultCount();
	}
	
	@Override
	public List<ModeloPasta> getResultList() {
//		return new ArrayList<ModeloPasta>();
		return super.getResultList();
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
