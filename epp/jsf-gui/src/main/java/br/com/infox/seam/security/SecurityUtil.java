package br.com.infox.seam.security;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.security.Identity;
import org.jboss.seam.web.ServletContexts;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named(SecurityUtil.NAME)
@SessionScoped
public class SecurityUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "security";
    public static final String PAGES_PREFIX = "/pages";
    private static final LogProvider LOG = Logging.getLogProvider(SecurityUtil.class);
    
    private Map<String, Boolean> permissions = new ConcurrentHashMap<>();

    public boolean checkPage(String page) {
        if ( !isSessionContextActive() ) {
            return false;
        }
        if (!permissions.containsKey(page)) {
            try {
                permissions.put(page, Identity.instance().hasPermission(page, "access") && !Authenticator.instance().hasToSignTermoAdesao());
            } catch (LoginException e) {
                throw new RuntimeException(e);
            }
        }
        boolean hasPermission = permissions.get(page);
        if (!hasPermission) {
            LOG.info(MessageFormat.format("Bloqueado o acesso do perfil ''{0}'' para o recurso ''{1}''.", getIdentificadorPapelAtual(), page));
        }
        return hasPermission;
    }
    
    public boolean isPermitted(String resource) {
        return checkPage(resource);
    }
    
    public boolean checkPage() {
        HttpServletRequest request = ServletContexts.instance().getRequest();
        String servletPath = request.getServletPath();
        return checkPage(PAGES_PREFIX + servletPath);
    }
    
    public boolean hasRole(String roleName) {
    	return isSessionContextActive() && Identity.instance().hasRole(roleName);
    }
    
    public void clearPermissionCache() {
        permissions = new HashMap<>();
    }

    public boolean isLoggedIn() {
        return isSessionContextActive() && Identity.instance().isLoggedIn();
    }
    
    protected String getIdentificadorPapelAtual() {
        Papel papelAtual = Authenticator.getPapelAtual();
        return papelAtual != null ? papelAtual.getIdentificador() : "";
    }
    
    public boolean isSessionContextActive() {
        return Beans.isActive(SessionScoped.class);
    }
    
    public static SecurityUtil instance() {
        return Beans.getReference(SecurityUtil.class);
    }
}
