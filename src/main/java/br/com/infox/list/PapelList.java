package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.access.entity.Papel;
import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;

@Name(PapelList.NAME)
@Scope(ScopeType.PAGE)
public class PapelList extends EntityList<Papel> {
	
	public static final String NAME = "papelList";
	private static final long serialVersionUID = 1L;
	
	/*
	 * Papel e Recurso fazem parte da mesma entidade, 
	 * a diferença consiste em que o identificador dos 
	 * recursos começa com '/'
	 * */
	public static final String DEFAULT_EJBQL = "select o from Papel o where identificador not like '/%'";
	public static final String DEFAULT_ORDER = "o.nome";
	
	private static final String R1 = "lower(nome) like concat('%',lower(#{papelList.entity.nome}),'%')";
	private static final String R2 = "lower(identificador) like concat('%',lower(#{papelList.entity.identificador}),'%')";

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
