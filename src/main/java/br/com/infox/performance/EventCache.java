package br.com.infox.performance;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.itx.util.ComponentUtil;

@Name(EventCache.NAME)
@Scope(ScopeType.EVENT)
@BypassInterceptors
public class EventCache extends AbstractCache {

	public static final String NAME = "eventCache";

	@Override
	@Observer("org.jboss.seam.preDestroy." + NAME)
	public void printEstatistica() {
		super.printEstatistica();
	}

	public static EventCache instance() {
		return ComponentUtil.getComponent(NAME, ScopeType.EVENT);
	}

}