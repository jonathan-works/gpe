package br.com.infox.epa.dao;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epa.entity.ProcessoEpa;
import br.com.infox.epa.query.ProcessoEpaQuery;

/**
 * Classe DAO para a entidade ProcessoEpa
 * @author Daniel
 *
 */
@Name(ProcessoEpaDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoEpaDAO extends GenericDAO {

	public static final String NAME = "processoEpaDAO";

	public List<ProcessoEpa> listAllNotEnded() {
		List<ProcessoEpa> resultList = getNamedResultList
			(ProcessoEpaQuery.LIST_ALL_NOT_ENDED, null);
		return resultList;
	}
	
}