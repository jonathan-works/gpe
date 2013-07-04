package br.com.infox.epp.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ProcessoTarefaEventoDAO;

@Name(ProcessoTarefaEventoManager.NAME)
@AutoCreate
public class ProcessoTarefaEventoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "processoTarefaEventoManager";
	
	@In private ProcessoTarefaEventoDAO processoTarefaEventoDAO;
	
}
