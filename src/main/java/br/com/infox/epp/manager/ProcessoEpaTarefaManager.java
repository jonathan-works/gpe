package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.ProcessoEpaTarefaDAO;
import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.entity.ProcessoEpaTarefa;
import br.com.infox.ibpm.type.PrazoEnum;

@Name(ProcessoEpaTarefaManager.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoEpaTarefaManager extends GenericManager {

	private static final long serialVersionUID = 7702766272346991620L;

	public static final String NAME = "processoEpaTarefaManager";

	@In
	private ProcessoEpaTarefaDAO processoEpaTarefaDAO;
	
	public ProcessoEpaTarefa getByTaskInstance(Long taskInstance) {
		return processoEpaTarefaDAO.getByTaskInstance(taskInstance);
	}
	
	public List<ProcessoEpaTarefa> getTarefaNotEnded(PrazoEnum tipoPrazo) {
		return processoEpaTarefaDAO.getTarefaNotEnded(tipoPrazo);
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