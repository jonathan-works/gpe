package br.com.infox.epp.access.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.BloqueioUsuario;

@Name(BloqueioUsuarioList.NAME)
@Scope(ScopeType.PAGE)
public class BloqueioUsuarioList extends EntityList<BloqueioUsuario> {
	
	public static final String NAME = "bloqueioUsuarioList";
	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from BloqueioUsuario o";
	private static final String DEFAULT_ORDER = "dataBloqueio";
	private static final String R1 = "o.usuario = #{usuarioHome.instance}";

	@Override
	protected void addSearchFields() {
		addSearchField("usuario", SearchCriteria.IGUAL, R1);
		addSearchField("dataBloqueio", SearchCriteria.DATA_IGUAL);
		addSearchField("dataPrevisaoDesbloqueio",SearchCriteria.DATA_IGUAL);
		addSearchField("dataDesbloqueio", SearchCriteria.DATA_IGUAL);
		addSearchField("motivoBloqueio", SearchCriteria.CONTENDO);
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
