package br.com.infox.epp.list;

import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.Natureza;
import br.com.infox.epp.entity.NaturezaCategoriaFluxo;
import br.com.infox.ibpm.entity.Fluxo;
import br.com.itx.util.EntityUtil;

@Name(NaturezaCategoriaFluxoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaCategoriaFluxoList extends EntityList<NaturezaCategoriaFluxo> {
	private static final long	serialVersionUID	= 1L;
	private static final String DEFAULT_EJBQL = "select o from NaturezaCategoriaFluxo o";
	private static final String DEFAULT_ORDER = "fluxo";
	public static final String NAME = "naturezaCategoriaFluxoList";

	@Override
	protected void addSearchFields() {
		addSearchField("natureza", SearchCriteria.igual);
		addSearchField("categoria", SearchCriteria.igual);
		addSearchField("fluxo", SearchCriteria.igual);
	}

	@Override
	protected String getDefaultEjbql() {
		return NaturezaCategoriaFluxoList.DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultOrder() {
		return NaturezaCategoriaFluxoList.DEFAULT_ORDER;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}
	
	public List<Natureza> getNaturezaList() {
		return EntityUtil.getEntityList(Natureza.class);
	}
	
	public List<Categoria> getCategoriaList() {
		return EntityUtil.getEntityList(Categoria.class);
	}
	
	public List<Fluxo> getFluxoList() {
		return EntityUtil.getEntityList(Fluxo.class);
	}

}
