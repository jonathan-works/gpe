package br.com.infox.ibpm.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(BloqueioUsuarioDAO.NAME)
@AutoCreate
public class BloqueioUsuarioDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "bloqueioUsuarioDAO";

}
