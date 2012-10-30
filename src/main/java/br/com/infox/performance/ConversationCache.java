package br.com.infox.performance;

import java.io.Serializable;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.itx.util.ComponentUtil;

@Name(ConversationCache.NAME)
@Scope(ScopeType.CONVERSATION)
@BypassInterceptors
public class ConversationCache extends AbstractCache implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "conversationCache";

	@Override
	@Observer("org.jboss.seam.preDestroy." + NAME)
	public void printEstatistica() {
		super.printEstatistica();
	}

	public static ConversationCache instance() {
		return ComponentUtil.getComponent(NAME, ScopeType.CONVERSATION);
	}

}