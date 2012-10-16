package br.com.infox.ibpm.dao;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.ProcessoEvento;
import br.com.infox.ibpm.query.ProcessoEventoQuery;

/**
 * Classe DAO para ProcessoEvento, contendo consultas a base.
 * @author Daniel
 *
 */
@Name(ProcessoEventoDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoEventoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "processoEventoDAO";
	
	/**
	 * Lista todos os eventos lançados para um determinado processo
	 * e que não foram processados na tabela de estatística.
	 * @return lista de processEvento não processado.
	 */
	public List<ProcessoEvento> listEventoNaoProcessado() {
		List<ProcessoEvento> namedResultList = getNamedResultList(ProcessoEventoQuery.LIST_EVENTO_NAO_PROCESSADO, null);
		return namedResultList;
	}
	
}