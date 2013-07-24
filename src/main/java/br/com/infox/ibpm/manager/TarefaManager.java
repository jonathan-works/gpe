package br.com.infox.ibpm.manager;

import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.ibpm.dao.TarefaDAO;
import br.com.infox.ibpm.entity.Tarefa;

@Name(TarefaManager.NAME)
@AutoCreate
public class TarefaManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaManager";
	
	@In private TarefaDAO tarefaDAO;
	
	public List<SelectItem> getPreviousTasks(Tarefa tarefa){
		return tarefaDAO.getPreviousTasks(tarefa);
	}
	
	public void encontrarNovasTarefas(){
		tarefaDAO.encontrarNovasTarefas();
	}

}
