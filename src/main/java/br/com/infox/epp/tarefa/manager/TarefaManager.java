package br.com.infox.epp.tarefa.manager;

import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.tarefa.dao.TarefaDAO;
import br.com.infox.epp.tarefa.entity.Tarefa;

@Name(TarefaManager.NAME)
@AutoCreate
public class TarefaManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "tarefaManager";
	
	@In private TarefaDAO tarefaDAO;
	
	public List<SelectItem> getPreviousNodes(Tarefa tarefa){
		return tarefaDAO.getPreviousNodes(tarefa);
	}
	
	public void encontrarNovasTarefas(){
		tarefaDAO.encontrarNovasTarefas();
	}

}
