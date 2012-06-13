package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.entity.Status;

@Name("statusHome")
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class StatusHome extends AbstractStatusHome<Status> {

	private static final long serialVersionUID = 1L;

	@Override
	public String persist() {
		String persist = null;
		persist = super.persist();
		return persist;
	}
}