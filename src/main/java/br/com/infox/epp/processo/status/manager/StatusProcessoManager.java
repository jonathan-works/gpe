package br.com.infox.epp.processo.status.manager;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.processo.status.dao.StatusProcessoDao;
import br.com.infox.epp.processo.status.entity.StatusProcesso;

@AutoCreate
@Stateless
@Name(StatusProcessoManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class StatusProcessoManager extends Manager<StatusProcessoDao, StatusProcesso> {
    
	public static final String NAME = "statusProcessoManager";
	private static final long serialVersionUID = 1L;
}
