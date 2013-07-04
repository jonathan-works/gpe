package br.com.infox.epp.manager;

import java.util.List;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.GenericManager;
import br.com.infox.epp.dao.AgrupamentoDAO;
import br.com.infox.ibpm.entity.Agrupamento;
import br.com.infox.ibpm.entity.TarefaEvento;

@Name(AgrupamentoManager.NAME)
@AutoCreate
public class AgrupamentoManager extends GenericManager {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "agrupamentoManager";
	
	@In private AgrupamentoDAO agrupamentoDAO;
	
	public List<Agrupamento> getAgrupamentosByTarefaEvento(TarefaEvento tarefaEvento){
		return agrupamentoDAO.getAgrupamentosByTarefaEvento(tarefaEvento);
	}

}
