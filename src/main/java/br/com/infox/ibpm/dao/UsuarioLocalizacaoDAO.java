package br.com.infox.ibpm.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(UsuarioLocalizacaoDAO.NAME)
@AutoCreate
public class UsuarioLocalizacaoDAO extends GenericDAO {

	public static final String NAME = "usuarioLocalizacaoDAO";
	private static final long serialVersionUID = 1L;

}
