package br.com.infox.access;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.FRAMEWORK;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.permission.PersistentPermissionResolver;

/**
 * Sobrescreve a classe original do Seam, fazendo cache da permissão
 * no PageContext
 * 
 * @author luizruiz
 *
 */

//TODO Verificar como invalidar o cache

@Name("org.jboss.seam.security.persistentPermissionResolver")
@Scope(APPLICATION)
@BypassInterceptors
@Install(precedence=FRAMEWORK)
@Startup
public class CachedPersistentPermissionResolver extends
		PersistentPermissionResolver {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean hasPermission(Object target, String action) {
		String hash = CachedPermissionResolver.getHash(this.getClass(), target, action);
		Object object = Contexts.getPageContext().get(hash);
		if (object != null) {
			return (Boolean) object;
		}
		boolean b = super.hasPermission(target, action);
		Contexts.getPageContext().set(hash, b);
		return b;
	}
	
}