package br.com.infox.epp.processo.status.dao;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.processo.status.entity.StatusProcesso;

@Stateless
@AutoCreate
@Name(StatusProcessoDao.NAME)
public class StatusProcessoDao extends DAO<StatusProcesso> {
    
	public static final String NAME = "statusProcessoDao";
	private static final long serialVersionUID = 1L;

}
