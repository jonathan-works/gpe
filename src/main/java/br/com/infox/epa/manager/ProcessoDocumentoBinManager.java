package br.com.infox.epa.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;

@Name(ProcessoDocumentoBinManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class ProcessoDocumentoBinManager extends GenericManager{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoDocumentoBinManager";	
}
