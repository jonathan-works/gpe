package br.com.infox.ibpm.manager;

import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.SituacaoProcessoDAO;

@Name(SituacaoProcessoManager.NAME)
@AutoCreate
public class SituacaoProcessoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "situacaoProcessoManager";
	
	@In private SituacaoProcessoDAO situacaoProcessoDAO;
	
	public boolean existemTarefasEmAberto(long taskId){
		return situacaoProcessoDAO.getQuantidadeTarefasAtivasByTaskId(taskId) > 0;
	}
	
	public List<Integer> getProcessosAbertosByIdTarefa(Integer idTarefa, Map<String, Object> selected) {
		return situacaoProcessoDAO.getProcessosAbertosByIdTarefa(idTarefa, selected);
	}
	
	public boolean canOpenTask(long currentTaskId) {
	    return situacaoProcessoDAO.canOpenTask(currentTaskId);
	}

}
