package br.com.infox.epp.access.api;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.security.management.JpaIdentityStore;
import org.jboss.seam.util.Reflections;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(RolesMap.NAME)
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Startup
public class RolesMap {

    public static final String NAME = "rolesMap";
    private static final LogProvider LOG = Logging.getLogProvider(RolesMap.class);

    private Map<String, Set<String>> roles = new HashMap<String, Set<String>>();

    public Set<String> getChildrenRoles(String role) {
        if (roles.containsKey(role)) {
            return roles.get(role);
        }
        Set<String> children = getChildren(role);
        roles.put(role, children);
        return children;
    }

    /**
     * Busca os roles recursivamente, usando
     * JpaIdentityStore.addRoleAndMemberships
     * 
     * @param role
     * @return
     */
    private Set<String> getChildren(String role) {
        Set<String> roleSet = new HashSet<String>();
        try {
            Method method = JpaIdentityStore.class.getDeclaredMethod("addRoleAndMemberships", String.class, Set.class);
            method.setAccessible(true);
            Reflections.invokeAndWrap(method, IdentityManager.instance().getIdentityStore(), role, roleSet);
        } catch (Exception e) {
            LOG.error(".getChildren()", e);
        }
        return roleSet;
    }

    public void clear() {
        roles.clear();
    }

    public static final RolesMap instance() {
        return (RolesMap) Component.getInstance(NAME, ScopeType.APPLICATION);
    }

}
