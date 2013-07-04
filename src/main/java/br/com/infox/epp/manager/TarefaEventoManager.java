package br.com.infox.epp.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;

@Name(TarefaEventoManager.NAME)
@AutoCreate
public class TarefaEventoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaEventoManager";

}
