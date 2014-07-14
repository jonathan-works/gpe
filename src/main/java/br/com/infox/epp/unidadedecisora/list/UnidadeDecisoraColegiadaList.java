package br.com.infox.epp.unidadedecisora.list;

import java.util.Map;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;

public class UnidadeDecisoraColegiadaList extends EntityList<UnidadeDecisoraColegiada>{

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from UnidadeDecisoraColegiada o";
    private static final String DEFAULT_ORDER = "nome";
    
    private UnidadeDecisoraMonocratica unidadeDecisoraMonocratica;

	@Override
	protected void addSearchFields() {
		addSearchField("nome", SearchCriteria.CONTENDO);
		addSearchField("ativo", SearchCriteria.IGUAL);
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

	public UnidadeDecisoraMonocratica getUnidadeDecisoraMonocratica() {
		return unidadeDecisoraMonocratica;
	}

	public void setUnidadeDecisoraMonocratica(UnidadeDecisoraMonocratica unidadeDecisoraMonocratica) {
		this.unidadeDecisoraMonocratica = unidadeDecisoraMonocratica;
	}

}
