package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.TwitterTemplate;
import br.com.itx.util.ComponentUtil;

@Name(TwitterTemplateList.NAME)
@BypassInterceptors
@Scope(ScopeType.CONVERSATION)
public class TwitterTemplateList extends EntityList<TwitterTemplate> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "twitterTemplateList";
	private static final String DEFAULT_EJBQL = "select o from TwitterTemplate o";
	private static final String DEFAULT_ORDER = "titulo";

	public static final TwitterTemplateList instance() {
	    return ComponentUtil.getComponent(NAME);
	}
	
	@Override
	protected void addSearchFields() {
		addSearchField("titulo", SearchCriteria.CONTENDO);
		addSearchField("mensagem", SearchCriteria.CONTENDO);
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

}
