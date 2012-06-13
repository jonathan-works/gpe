package br.com.infox.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Assunto;

@Name(AssuntoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class AssuntoList extends EntityList<Assunto> {
	
	public static final String NAME = "assuntoList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Assunto o";
	private static final String DEFAULT_ORDER = "caminhoCompleto";
	
	/**
	 * Restricao por seleção de um assunto (o.assuntoPai)
	 */
	private static final String R1 = "o.caminhoCompleto like concat(" +
									"#{assuntoList.entity.assuntoPai.caminhoCompleto}, '%')";


	protected void addSearchFields() {
		addSearchField("assunto", SearchCriteria.contendo);
		addSearchField("assuntoPai", SearchCriteria.contendo, R1);
		addSearchField("codAssunto", SearchCriteria.contendo);
		addSearchField("ativo", SearchCriteria.igual);
	}

	protected Map<String, String> getCustomColumnsOrder() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("assuntoPai", "assuntoPai.assunto");
		return map;
	}

	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}
	


}