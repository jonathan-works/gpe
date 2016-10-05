package br.com.infox.epp.fluxo.list;

import java.util.Map;

import org.jboss.seam.annotations.Name;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.fluxo.entity.ModeloPasta;

@Name(ModeloPastaList.NAME)
public class ModeloPastaList extends EntityList<ModeloPasta>{

	private static final long serialVersionUID = 1L;
	static final String NAME = "modeloPastaList";
	
	private final String DEFAULT_EJBQL = "select o from ModeloPasta o";
	private final String DEFAULT_ORDER = "ordem";
	
	public void clearSearchFields() {
		getEntity().setCodigo(null);
	    getEntity().setNome(null);
	    getEntity().setDescricao(null);
	}
	
	@Override
	protected void addSearchFields() {
		addSearchField("nome", SearchCriteria.CONTENDO);
		addSearchField("codigo", SearchCriteria.CONTENDO);
		addSearchField("descricao", SearchCriteria.CONTENDO);
		addSearchField("fluxo", SearchCriteria.IGUAL);
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
