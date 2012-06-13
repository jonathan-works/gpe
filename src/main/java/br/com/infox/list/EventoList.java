package br.com.infox.list;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.Evento;

@Name(EventoList.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class EventoList extends EntityList<Evento> {
	
	public static final String NAME = "eventoList";

	private static final long serialVersionUID = 1L;
	
	private static final String DEFAULT_EJBQL = "select o from Evento o";
	private static final String DEFAULT_ORDER = "caminhoCompleto";

	private static final String R1 = "o.caminhoCompleto like concat(" +
									"#{eventoList.entity.eventoSuperior.caminhoCompleto}, '%')";

	public EventoList() {
		super();
		setEjbql(DEFAULT_EJBQL);
		setOrder(DEFAULT_ORDER);
		setMaxResults(DEFAULT_MAX_RESULT);
	}
	 
	protected void addSearchFields() {
		addSearchField("evento", SearchCriteria.contendo);
		addSearchField("eventoSuperior", SearchCriteria.contendo, R1);
		addSearchField("observacao", SearchCriteria.contendo);
		addSearchField("status", SearchCriteria.igual);
		addSearchField("ativo", SearchCriteria.igual);
	}

	protected Map<String, String> getCustomColumnsOrder() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("eventoSuperior", "eventoSuperior.evento");
		map.put("status", "status.status");
		return map;
	}

	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

}