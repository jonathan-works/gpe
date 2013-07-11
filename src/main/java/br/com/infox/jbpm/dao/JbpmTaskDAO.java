package br.com.infox.jbpm.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;

@Name(JbpmTaskDAO.NAME)
@AutoCreate
public class JbpmTaskDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "jbpmTaskDAO";

}
