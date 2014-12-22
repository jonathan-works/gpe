package br.com.infox.epp.julgamento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.julgamento.dao.StatusSessaoJulgamentoDAO;
import br.com.infox.epp.julgamento.entity.StatusSessaoJulgamento;

@AutoCreate
@Name(StatusSessaoJulgamentoManager.NAME)
public class StatusSessaoJulgamentoManager extends Manager<StatusSessaoJulgamentoDAO, StatusSessaoJulgamento>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "statusSessaoJulgamentoManager";
	
	public StatusSessaoJulgamento getStatusSessaoJulgamentoByNome(String nome) {
        return getDao().getStatusSessaoJulgamentoByNome(nome);
    }

}
