package br.com.infox.epp.processo.status.manager;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.processo.status.dao.StatusProcessoDao;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.seam.exception.BusinessException;

@AutoCreate
@Stateless
@Name(StatusProcessoManager.NAME)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class StatusProcessoManager extends Manager<StatusProcessoDao, StatusProcesso> {
    
	public static final String NAME = "statusProcessoManager";
	private static final long serialVersionUID = 1L;
	
	@Inject
	private StatusProcessoSearch statusProcessoSearch;
	
	@Inject 
	private InfoxMessages infoxMessages;
	
	public void validateBeforePersist(StatusProcesso statusProcesso) {
		StatusProcesso statusByName = statusProcessoSearch.getStatusByName(statusProcesso.getNome());
		if(statusByName != null) {
			throw new BusinessException(infoxMessages.get("statusProcesso.erroJaCadastrado"));
		}
	}
}
