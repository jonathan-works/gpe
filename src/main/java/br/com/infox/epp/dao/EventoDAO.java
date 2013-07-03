package br.com.infox.epp.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(EventoDAO.NAME)
@AutoCreate
public class EventoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "eventoDAO";

}
