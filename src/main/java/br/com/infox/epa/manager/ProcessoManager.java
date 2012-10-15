package br.com.infox.epa.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;

@Name(ProcessoManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoManager extends GenericManager {
	
	public static final String NAME = "processoManager";
	
}
