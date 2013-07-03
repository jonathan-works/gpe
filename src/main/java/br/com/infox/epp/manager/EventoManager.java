package br.com.infox.epp.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;

@Name(EventoManager.NAME)
@AutoCreate
public class EventoManager extends GenericManager {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "eventoManager";

}
