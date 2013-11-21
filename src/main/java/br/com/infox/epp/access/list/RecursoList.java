package br.com.infox.epp.access.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.Papel;

@Name(RecursoList.NAME)
@Scope(ScopeType.PAGE)
public class RecursoList extends EntityList<Papel> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "recursoList";

	/*
	 * Papel e Recurso fazem parte da mesma entidade, 
	 * a diferença consiste em que o identificador dos 
	 * recursos começa com '/'
	 * */
	public static final String DEFAULT_EJBQL = "select o from Papel o where identificador like '/%'";
	public static final String DEFAULT_ORDER = "o.nome";
	
	private static final String R1 = "lower(nome) like concat('%',lower(#{recursoList.entity.nome}),'%')";
	private static final String R2 = "lower(identificador) like concat('%',lower(#{recursoList.entity.identificador}),'%')";

	@Override
	protected void addSearchFields() {
		addSearchField("nome", SearchCriteria.CONTENDO, R1);
		addSearchField("identificador", SearchCriteria.CONTENDO, R2);
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
