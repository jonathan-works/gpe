package br.com.infox.epp.processo.situacao.dao;

import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.COUNT_TAREFAS_ATIVAS_BY_TASK_ID;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.ID_TAREFA_PARAM;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PARAM_ID_TASKINSTANCE;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PROCESSOS_ABERTOS;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PROCESSOS_ABERTOS_EM_CAIXA;
import static br.com.infox.epp.processo.situacao.query.SituacaoProcessoQuery.PROCESSOS_ABERTOS_SEM_CAIXA;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;
import br.com.infox.ibpm.util.JbpmUtil;

@Name(SituacaoProcessoDAO.NAME)
@AutoCreate
public class SituacaoProcessoDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "situacaoProcessoDAO";
	
    public Long getQuantidadeTarefasAtivasByTaskId(long taskId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARAM_ID_TASKINSTANCE, taskId);
        return getNamedSingleResult(COUNT_TAREFAS_ATIVAS_BY_TASK_ID, parameters);
    }

    public List<Integer> getProcessosAbertosByIdTarefa(Integer idTarefa, Map<String, Object> selected) {
        String namedQuery = getTreeTypeRestriction(selected);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(ID_TAREFA_PARAM, idTarefa);
        return getNamedResultList(namedQuery, parameters);
    }

    private String getTreeTypeRestriction(Map<String, Object> selected) {
        String treeType = (String) selected.get("tree");
        String nodeType = (String) selected.get("type");
        if ("caixa".equals(treeType) && "Task".equals(nodeType)) {
            return PROCESSOS_ABERTOS_SEM_CAIXA;
        }
        if (treeType == null && "Caixa".equals(nodeType)) {
            return PROCESSOS_ABERTOS_EM_CAIXA;
        }
        return PROCESSOS_ABERTOS;
    }

    public boolean canOpenTask(long currentTaskId) {
        JbpmUtil.getJbpmSession().flush();
        Events.instance().raiseEvent(TarefasTreeHandler.FILTER_TAREFAS_TREE);
        Long count = getQuantidadeTarefasAtivasByTaskId(currentTaskId);
        return count != null && count > 0;
    }

}
