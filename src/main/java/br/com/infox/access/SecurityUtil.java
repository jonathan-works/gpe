package br.com.infox.access;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.Identity;
import org.jboss.seam.web.ServletContexts;

import br.com.itx.util.ComponentUtil;

@Name("security")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
public class SecurityUtil {

	public static final String PAGES_PREFIX = "/pages";
	private static final LogProvider log = Logging.getLogProvider(SecurityUtil.class);

	public boolean checkPage() {
		HttpServletRequest request = ServletContexts.instance().getRequest();
		String servletPath = request.getServletPath();
		boolean hasRole = Identity.instance().hasRole(PAGES_PREFIX + servletPath);
		if (!hasRole) {
			log.info(MessageFormat.format(
					"Bloqueado o acesso do perfil ''{0}'' para página ''{1}''.",
					Contexts.getSessionContext().get("identificadorPapelAtual"), servletPath));
		}
		return hasRole;
	}
	
	public static SecurityUtil instance() {
		return ComponentUtil.getComponent("security");
	}
	
}