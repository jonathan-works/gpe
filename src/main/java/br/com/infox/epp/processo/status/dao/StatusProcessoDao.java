package br.com.infox.epp.processo.status.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso_;

@Stateless
@AutoCreate
@Name(StatusProcessoDao.NAME)
public class StatusProcessoDao extends DAO<StatusProcesso> {
    
	public static final String NAME = "statusProcessoDao";
	private static final long serialVersionUID = 1L;
	
	

}
