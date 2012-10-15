package br.com.infox.epa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epa.entity.Categoria;
import br.com.infox.epa.entity.ProcessoEpaTarefa;
import br.com.infox.epa.query.ProcessoEpaTarefaQuery;

/**
 * Classe DAO para a entidade ProcessoEpaTarefa
 * @author Daniel
 *
 */
@Name(ProcessoEpaTarefaDAO.NAME)
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ProcessoEpaTarefaDAO extends GenericDAO {

	public static final String NAME = "processoEpaTarefaDAO";
	
	/**
	 * Lista todos os registros filtrando por uma natureza.
	 * @param natureza que se desejar filtrar a seleção.
	 * @return lista de todos os registros referente a <code>natureza</code>
	 * informada.
	 */
	public ProcessoEpaTarefa getByTaskInstance(Long taskInstance) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ProcessoEpaTarefaQuery.QUERY_PARAM_TASKINSTANCE, taskInstance);
		ProcessoEpaTarefa result = getNamedSingleResult(
						ProcessoEpaTarefaQuery.GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE, 
						parameters);
		return result;		
	}

	public List<ProcessoEpaTarefa> getAllNotEnded() {
		return getNamedResultList(ProcessoEpaTarefaQuery.LIST_ALL_NOT_ENDED, null);
	}
	
	public List<Object[]> listForaPrazoFluxo(Categoria c) {
		StringBuilder s = new StringBuilder();
		s.append(ProcessoEpaTarefaQuery.QUERY_FORA_FLUXO)
		 .append("where p.porcentagem > 100 and pt.dataFim is null ")
		 .append("and c = :categoria");
		Query q = entityManager.createQuery(s.toString());
		q.setParameter("categoria", c);
		List<Object[]> resultList = q.getResultList();
		return resultList;
	}
	
	public List<Object[]> listForaPrazoTarefa(Categoria c) {
		StringBuilder s = new StringBuilder();
		s.append(ProcessoEpaTarefaQuery.QUERY_FORA_FLUXO)
		 .append("where pt.porcentagem > 100 and pt.dataFim is null ")
		 .append("and c = :categoria");
		Query q = entityManager.createQuery(s.toString());
		q.setParameter("categoria", c);
		List<Object[]> resultList = q.getResultList();
		return resultList;
	}
	
	public List<Object[]> listTarefaPertoLimite() {
		StringBuilder s = new StringBuilder();
		s.append(ProcessoEpaTarefaQuery.QUERY_FORA_FLUXO)
		 .append("where pt.porcentagem <= 100 and pt.porcentagem >= 70 ")
		 .append("and pt.dataFim is null");
		Query q = entityManager.createQuery(s.toString());
		List<Object[]> resultList = q.getResultList();
		return resultList;
	}
	
}