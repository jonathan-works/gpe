package br.com.infox.epa.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epa.dao.ProcessoEpaTarefaDAO;
import br.com.infox.epa.entity.Categoria;
import br.com.infox.epa.entity.ProcessoEpaTarefa;

@Name(ProcessoEpaTarefaManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoEpaTarefaManager extends GenericManager {

	public static final String NAME = "processoEpaTarefaManager";

	@In
	private ProcessoEpaTarefaDAO processoEpaTarefaDAO;
	
	public ProcessoEpaTarefa getByTaskInstance(Long taskInstance) {
		return processoEpaTarefaDAO.getByTaskInstance(taskInstance);
	}
	
	public List<ProcessoEpaTarefa> getAllNotEnded() {
		return processoEpaTarefaDAO.getAllNotEnded();
	}
	
	public List<Object[]> listForaPrazoFluxo(Categoria c) {
		return processoEpaTarefaDAO.listForaPrazoFluxo(c);
	}
	
	public List<Object[]> listForaPrazoTarefa(Categoria c) {
		return processoEpaTarefaDAO.listForaPrazoTarefa(c);
	}
	
	public List<Object[]> listTarefaPertoLimite() {
		return processoEpaTarefaDAO.listTarefaPertoLimite();
	}
	
}