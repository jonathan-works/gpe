package br.com.infox.epa.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;

@Name(AgrupamentoDAO.NAME)
@Scope(ScopeType.EVENT)
public class AgrupamentoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "agrupamentoDao";
	
}
