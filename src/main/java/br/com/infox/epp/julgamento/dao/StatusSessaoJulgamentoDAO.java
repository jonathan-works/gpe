package br.com.infox.epp.julgamento.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.julgamento.entity.StatusSessaoJulgamento;

@AutoCreate
@Name(StatusSessaoJulgamentoDAO.NAME)
public class StatusSessaoJulgamentoDAO extends DAO<StatusSessaoJulgamento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "statusSessaoJulgamentoDAO";

}
