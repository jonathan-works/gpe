package br.com.infox.epp.tarefa.dao;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.PARAM_ID_PROCESSO;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.PARAM_ID_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.PROCESSO_EPA_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.BASE_QUERY_FORA_FLUXO;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.QUERY_PARAM_TASKINSTANCE;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.QUERY_PARAM_TIPO_PRAZO;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.TAREFA_ENDED;
import static br.com.infox.epp.tarefa.query.ProcessoEpaTarefaQuery.TAREFA_NOT_ENDED_BY_TIPO_PRAZO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.tarefa.entity.ProcessoEpaTarefa;
import br.com.infox.epp.tarefa.type.PrazoEnum;

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
        parameters.put(QUERY_PARAM_TASKINSTANCE, taskInstance);
        return getNamedSingleResult(GET_PROCESSO_EPA_TAREFA_BY_TASKINSTNACE, parameters);
    }

    public List<ProcessoEpaTarefa> getTarefaEnded() {
        return getNamedResultList(TAREFA_ENDED);
    }

    public List<ProcessoEpaTarefa> getTarefaNotEnded(PrazoEnum tipoPrazo) {
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(QUERY_PARAM_TIPO_PRAZO, tipoPrazo);
        return getNamedResultList(TAREFA_NOT_ENDED_BY_TIPO_PRAZO, parameters);
    }
	
	@SuppressWarnings(UNCHECKED)
	public List<Object[]> listForaPrazoFluxo(Categoria c) {
		StringBuilder s = new StringBuilder();
		s.append(BASE_QUERY_FORA_FLUXO)
		 .append("where p.porcentagem > 100 and pt.dataFim is null ")
		 .append("and c = :categoria");
		Query q = getEntityManager().createQuery(s.toString());
		q.setParameter("categoria", c);
		return q.getResultList();
	}
	
	@SuppressWarnings(UNCHECKED)
	public List<Object[]> listForaPrazoTarefa(Categoria c) {
		StringBuilder s = new StringBuilder();
		s.append(BASE_QUERY_FORA_FLUXO)
		 .append("where pt.porcentagem > 100 and pt.dataFim is null ")
		 .append("and c = :categoria");
		Query q = getEntityManager().createQuery(s.toString());
		q.setParameter("categoria", c);
		return q.getResultList();
	}
	
	@SuppressWarnings(UNCHECKED)
	public List<Object[]> listTarefaPertoLimite() {
		StringBuilder s = new StringBuilder();
		s.append(BASE_QUERY_FORA_FLUXO)
		 .append("where pt.porcentagem <= 100 and pt.porcentagem >= 70 ")
		 .append("and pt.dataFim is null");
		Query q = getEntityManager().createQuery(s.toString());
		return q.getResultList();
	}
	
    public Map<String, Object> findProcessoEpaTarefaByIdProcessoAndIdTarefa(final Integer idProcesso, final Integer idTarefa) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_PROCESSO, idProcesso);
        parameters.put(PARAM_ID_TAREFA, idTarefa);
        return getNamedSingleResult(PROCESSO_EPA_TAREFA_BY_ID_PROCESSO_AND_ID_TAREFA, parameters);
    }
}