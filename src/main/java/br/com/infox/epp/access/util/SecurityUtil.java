package br.com.infox.epp.access.util;

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

import br.com.infox.seam.util.ComponentUtil;

@Name("security")
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
//TODO verificar se esta classe não se encaixaria melhor em br.com.infox.seam
public class SecurityUtil {

    public static final String PAGES_PREFIX = "/pages";
    private static final LogProvider LOG = Logging.getLogProvider(SecurityUtil.class);

    public boolean checkPage() {
        HttpServletRequest request = ServletContexts.instance().getRequest();
        String servletPath = request.getServletPath();
        boolean hasPermission = Identity.instance().hasPermission(PAGES_PREFIX
                + servletPath, "access");
        if (!hasPermission) {
            LOG.info(MessageFormat.format("Bloqueado o acesso do perfil ''{0}'' para página ''{1}''.", Contexts.getSessionContext().get("identificadorPapelAtual"), servletPath));
        }
        return hasPermission;
    }

    public static SecurityUtil instance() {
        return ComponentUtil.getComponent("security");
    }

}
