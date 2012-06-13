package br.com.infox.temp;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;

import br.com.infox.annotations.manager.RecursiveManager;
import br.com.infox.ibpm.entity.Assunto;
import br.com.infox.ibpm.entity.Evento;
import br.com.itx.component.Util;
import br.com.itx.util.EntityUtil;

@Name("populateHierarchicalPathsBpm")
@Scope(ScopeType.APPLICATION)
@Startup()
@Install(precedence=Install.FRAMEWORK)
public class PopulateHierarchicalPathsBpm {

	private static final Class<?>[] clazz = {Assunto.class, Evento.class};

	@Observer({"org.jboss.seam.postInitialization","org.jboss.seam.postReInitialization"})
	public void populate() {
		boolean b = Util.beginTransaction();
		for (Class<?> c : clazz) {
			RecursiveManager.populateAllHierarchicalPaths(c);
			EntityUtil.getEntityManager().flush();
			EntityUtil.getEntityManager().clear();
		}
		if(b) {
			Util.commitTransction();
		}
	}
	
}
