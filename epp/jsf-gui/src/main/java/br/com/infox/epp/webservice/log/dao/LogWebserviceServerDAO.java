package br.com.infox.epp.webservice.log.dao;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.webservice.log.entity.LogWebserviceServer;

@Stateless
@AutoCreate
@Name(LogWebserviceServerDAO.NAME)
public class LogWebserviceServerDAO extends DAO<LogWebserviceServer> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "logWebserviceServerDAO";

}
