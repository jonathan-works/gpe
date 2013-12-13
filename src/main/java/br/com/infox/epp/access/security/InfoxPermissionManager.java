package br.com.infox.epp.access.security;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.security.Principal;
import java.util.List;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.permission.PermissionManager;

import br.com.infox.epp.access.entity.Permissao;

@Scope(APPLICATION)
@Name("org.jboss.seam.security.permissionManager")
@Install(precedence = FRAMEWORK)
public class InfoxPermissionManager extends PermissionManager {

    private static final long serialVersionUID = 1L;
    
    
    public List<Permissao> getPermissoesFromRole(Role role){
        InfoxPermissionStore ips = (InfoxPermissionStore) getPermissionStore();
        return ips.getPermissoesFromRole(role);
    }
    
    public List<Permissao> getPermissoesFromUsuario(Principal principal){
        InfoxPermissionStore ips = (InfoxPermissionStore) getPermissionStore();
        return ips.getPermissoesFromUsuario(principal);
    }
    
    public static InfoxPermissionManager instance() {
        return instance();
    }
    
    @Override
    public InfoxPermissionStore getPermissionStore() {
        return (InfoxPermissionStore) super.getPermissionStore();
    }

}
