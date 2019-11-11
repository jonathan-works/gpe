package br.com.infox.ibpm.task.action;

import java.io.Serializable;

import javax.inject.Named;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;

@Named(TaskInstanceController.NAME)
@ViewScoped
public class TaskInstanceController implements Serializable {
	public static final String NAME = "taskInstanceController";
	private static final long serialVersionUID = 1L;

	public boolean podeExibirPrioridadeProcesso() {
		return Authenticator.isUsuarioAtualResponsavel();
	}
}
