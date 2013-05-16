package br.com.infox.epp.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(UsuarioLoginDAO.NAME)
@AutoCreate
public class UsuarioLoginDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "usuarioLoginDAO";
	
}
