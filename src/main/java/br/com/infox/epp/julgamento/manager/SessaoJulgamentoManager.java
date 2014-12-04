package br.com.infox.epp.julgamento.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.julgamento.dao.SessaoJulgamentoDAO;
import br.com.infox.epp.julgamento.entity.SessaoJulgamento;

@AutoCreate
@Name(SessaoJulgamentoManager.NAME)
public class SessaoJulgamentoManager extends Manager<SessaoJulgamentoDAO, SessaoJulgamento> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "sessaoJulgamentoManager";

}
