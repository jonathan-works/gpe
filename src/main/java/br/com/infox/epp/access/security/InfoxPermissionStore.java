package br.com.infox.epp.access.security;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import java.security.Principal;
import java.util.List;

import javax.persistence.Query;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.security.Role;
import org.jboss.seam.security.permission.JpaPermissionStore;

import br.com.infox.epp.access.entity.Permissao;

@Name("org.jboss.seam.security.jpaPermissionStore")
@Install(precedence = FRAMEWORK, value=false) 
@Scope(APPLICATION)
@BypassInterceptors
public class InfoxPermissionStore extends JpaPermissionStore{

    private static final long serialVersionUID = 1L;
    
    public List<Permissao> getPermissoesFromRole(Role role){
        Query query = createPermissionQuery(null, null, role, Discrimination.role);
        return query.getResultList();
    }
    
    public List<Permissao> getPermissoesFromUsuario(Principal principal){
        Query query = createPermissionQuery(null, null, principal, Discrimination.user);
        return query.getResultList();
    }
    
    

}
