package br.com.infox.epp.tarefa.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery;
import br.com.infox.epp.tarefa.type.PrazoEnum;
import br.com.itx.util.EntityUtil;

/**
 * Classe DAO para a entidade ProcessoEpaTarefa
 * @author Daniel
 *
 */
@Name(ProcessoEpaTarefaDAO.NAME)
@AutoCreate
public class ProcessoEpaTarefaDAO extends GenericDAO {

	private static final long serialVersionUID = 4132828408460655332L;
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

	public List<ProcessoEpaTarefa> getTarefaEnded() {
		return getNamedResultList(ProcessoEpaTarefaQuery.TAREFA_ENDED, null);
	}
	
	public List<ProcessoEpaTarefa> getTarefaNotEnded(PrazoEnum tipoPrazo) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(ProcessoEpaTarefaQuery.QUERY_PARAM_TIPO_PRAZO, tipoPrazo);
		return getNamedResultList(ProcessoEpaTarefaQuery.TAREFA_NOT_ENDED_BY_TIPO_PRAZO, parameters);
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<Object[]> listForaPrazoFluxo(Categoria c) {
		StringBuilder s = new StringBuilder();
		s.append(ProcessoEpaTarefaQuery.QUERY_FORA_FLUXO)
		 .append("where p.porcentagem > 100 and pt.dataFim is null ")
		 .append("and c = :categoria");
		Query q = getEntityManager().createQuery(s.toString());
		q.setParameter("categoria", c);
		return q.getResultList();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<Object[]> listForaPrazoTarefa(Categoria c) {
		StringBuilder s = new StringBuilder();
		s.append(ProcessoEpaTarefaQuery.QUERY_FORA_FLUXO)
		 .append("where pt.porcentagem > 100 and pt.dataFim is null ")
		 .append("and c = :categoria");
		Query q = getEntityManager().createQuery(s.toString());
		q.setParameter("categoria", c);
		return q.getResultList();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public List<Object[]> listTarefaPertoLimite() {
		StringBuilder s = new StringBuilder();
		s.append(ProcessoEpaTarefaQuery.QUERY_FORA_FLUXO)
		 .append("where pt.porcentagem <= 100 and pt.porcentagem >= 70 ")
		 .append("and pt.dataFim is null");
		Query q = getEntityManager().createQuery(s.toString());
		return q.getResultList();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
    public Map<String, Object> findProcessoEpaTarefaByIdProcessoAndIdTarefa(final Integer idProcesso, final Integer idTarefa) {
        final String hql = "select new map(pet.taskInstance as idTaskInstance) " +
                            "from ProcessoEpaTarefa pet " +
                            "where pet.tarefa.idTarefa=:idTarefa " +
                            "and pet.processoEpa.idProcesso=:idProcesso";
        final Query query = EntityUtil.createQuery(hql)
                            .setParameter("idProcesso",idProcesso)
                            .setParameter("idTarefa", idTarefa);
        return (Map<String, Object>) query.getSingleResult();
    }
}