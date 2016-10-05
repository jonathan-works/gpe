package br.com.infox.seam.security;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;
import org.jboss.seam.web.ServletContexts;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named(SecurityUtil.NAME)
@SessionScoped
public class SecurityUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "security";
    public static final String PAGES_PREFIX = "/pages";
    private static final LogProvider LOG = Logging.getLogProvider(SecurityUtil.class);
    
    private Map<String, Boolean> permissions = new HashMap<>();

    public boolean checkPage(String page) {
        if (!permissions.containsKey(page)) {
            try {
                if (!Contexts.isSessionContextActive())
                    return false;
                permissions.put(page, Identity.instance().hasPermission(page, "access") && !Authenticator.instance().hasToSignTermoAdesao());
            } catch (LoginException e) {
                throw new RuntimeException(e);
            }
        }
        boolean hasPermission = permissions.get(page);
        if (!hasPermission) {
            LOG.info(MessageFormat.format("Bloqueado o acesso do perfil ''{0}'' para o recurso ''{1}''.", Contexts.getSessionContext().get("identificadorPapelAtual"), page));
        }
        return hasPermission;
    }
    
    public boolean isPermitted(String resource) {
        if (!Contexts.isSessionContextActive())
            return false;
        return Identity.instance().hasPermission(resource, "access");
    }
    
    public boolean checkPage() {
        HttpServletRequest request = ServletContexts.instance().getRequest();
        String servletPath = request.getServletPath();
        return checkPage(PAGES_PREFIX + servletPath);
    }
    
    public boolean hasRole(String roleName) {
        if (!Contexts.isSessionContextActive())
            return false;
    	return Identity.instance().hasRole(roleName);
    }
    
    public void clearPermissionCache() {
        permissions = new HashMap<>();
    }

    public boolean isLoggedIn() {
        if (!Contexts.isSessionContextActive())
            return false;
        return Identity.instance().isLoggedIn();
    }
}
