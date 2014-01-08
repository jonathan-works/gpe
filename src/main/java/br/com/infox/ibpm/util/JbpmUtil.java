/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.ibpm.util;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;
import static br.com.infox.ibpm.util.JbpmQueries.ALL_TASKS_QUERY;
import static br.com.infox.ibpm.util.JbpmQueries.PROCESS_NAMES_QUERY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.hibernate.Session;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.bpm.BusinessProcess;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.db.GraphSession;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.SwimlaneInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.tarefa.entity.Tarefa;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.ibpm.variable.JbpmVariavelLabel;
import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;


@Name(JbpmUtil.NAME)
@Scope(ScopeType.APPLICATION)
@BypassInterceptors
@Install(dependencies={})
public class JbpmUtil {


	private static final LogProvider LOG = Logging.getLogProvider(JbpmUtil.class);
	
	public static final String NAME = "jbpmUtil";
	public static final int FROM_TASK_TRANSITION = 0;	
	public static final int TO_TASK_TRANSITION = 1;
	private static final String VAR_NOME_TAREFA_ANTERIOR = "nomeTarefaAnterior";
	private static Map<String, String> messagesMap;	
	
	/**
	 * Busca a localização de uma tarefa
	 * 
	 * @param task
	 * @return
	 */
	public Localizacao getLocalizacao(TaskInstance task) {
		SwimlaneInstance swimlaneInstance = task.getSwimlaneInstance();
		if (swimlaneInstance != null) {
			String expression = swimlaneInstance.getSwimlane().getPooledActorsExpression();
			if (expression == null) {
			    return null; 
			}
			//TODO: verificar se pode ser dado um tratamento melhor
			String localizacaoId = expression.substring(expression.indexOf('(') + 1);
			localizacaoId = localizacaoId.substring(0, localizacaoId.lastIndexOf(')'));
			if (localizacaoId.indexOf(':') > 0) {
				localizacaoId = localizacaoId.replaceAll("'", "");
				localizacaoId = localizacaoId.split(":")[0];
			}
			return EntityUtil.find(Localizacao.class, Integer.valueOf(localizacaoId));
		}
		return null;
	}

	/**
	 * Busca a localização de um processo
	 * 
	 * @param jbpmProcessId é o id do Processo no contexto jBPM (Processo.getIdJbpm())
	 * @return retorna a primeira localização encontrada
	 */
	public Localizacao getLocalizacao(long jbpmProcessId) {
		ProcessInstance pi = ManagedJbpmContext.instance().getProcessInstance(jbpmProcessId);
		Token token = pi.getRootToken();
		for (Object o : pi.getTaskMgmtInstance().getTaskInstances()) {
			TaskInstance t = (TaskInstance) o;
			if (t.getTask().getTaskNode().equals(token.getNode())) {
				return getLocalizacao(t);
			}
		}
		return null;
	}
	
	public static Session getJbpmSession() {
		return ManagedJbpmContext.instance().getSession();
	}
	
	@Factory(value="jbpmMessages", scope=ScopeType.APPLICATION)
	public Map<String, String> getMessages() {
		return getJbpmMessages();
	}
	
	public static synchronized Map<String, String> getJbpmMessages() {
		if (messagesMap == null) {
			messagesMap = new HashMap<String, String>();
			List<JbpmVariavelLabel> l = EntityUtil.getEntityList(JbpmVariavelLabel.class);
			for (JbpmVariavelLabel j : l) {
				messagesMap.put(j.getNomeVariavel(), j.getLabelVariavel());
			}
		}
		return messagesMap;
	}
	
    @SuppressWarnings(UNCHECKED)
    public List<String> getProcessNames() {
        return getJbpmSession().createQuery(PROCESS_NAMES_QUERY).list();
    }

    @SuppressWarnings(UNCHECKED)
    public List<TaskInstance> getAllTasks() {
        return getJbpmSession().createQuery(ALL_TASKS_QUERY).list();
    }
	
	@SuppressWarnings(UNCHECKED)
	public static <T> T getProcessVariable(String name) {
		ProcessInstance processInstance = org.jboss.seam.bpm.ProcessInstance.instance();
		if (processInstance != null) {
			ContextInstance contextInstance = processInstance.getContextInstance();
			return (T) contextInstance.getVariable(name);
		}
		return null;
	}
	
    public static void setProcessVariable(String name, Object value) {
        getConxtextInstance().setVariable(name, value);
    }

    public static void createProcessVariable(String name, Object value) {
        getConxtextInstance().createVariable(name, value);
    }

    private static ContextInstance getConxtextInstance() {
        return org.jboss.seam.bpm.ProcessInstance.instance().getContextInstance();
    }
	
	/**
	 * Retorna as tarefas (from / to) de uma transição
	 * Pode ocorrer null quando algum dos nós não é de tarefa
	 */
	public static Task[] getTasksFromTransition(Transition t) {
		Task[] ret = new Task[2];
		ret[0] = getTaskFromNode(t.getFrom());
		ret[1] = getTaskFromNode(t.getTo());
		return ret;
	}

	/**
	 * Retorna a tarefas de um nó
	 * Pode ocorrer null quando o nó não é de tarefa
	 */
	public static Task getTaskFromNode(Node node) {
		Task t = null;
		if (node.getNodeType().equals(NodeType.Task)) {
			TaskNode tn = (TaskNode) JbpmUtil.getJbpmSession().load(TaskNode.class, node.getId());
			if (!tn.getTasks().isEmpty()) {
				t = (Task) tn.getTasks().iterator().next(); 
			}
		}
		return t;
	}
	
	public Object getConteudo(VariableAccess var, TaskInstance taskInstance){
		String type = var.getMappedName().split(":")[0];
		Object variable = taskInstance.getVariable(var.getMappedName());
		
		if (isTypeEditor(type)){
			Integer id = (Integer) variable;
			if (id != null){
				ProcessoDocumento processoDocumento = EntityUtil.find(ProcessoDocumento.class, id);
				if (processoDocumento == null) {
					LOG.warn("ProcessoDocumento não encontrado: " + id);
				} else {
					variable = processoDocumento.getProcessoDocumentoBin().getModeloDocumento();
				}
			}
		}
		return variable;
	}

	public static JbpmUtil instance() {
		return (JbpmUtil) ComponentUtil.getComponent(NAME, ScopeType.APPLICATION);
	}
	
	public static GraphSession getGraphSession() {
		return new GraphSession(getJbpmSession());
	}
	
	public static Processo getProcesso() {
		Integer idProcesso = JbpmUtil.getProcessVariable("processo");
		return idProcesso != null ? EntityUtil.find(Processo.class, idProcesso) : null;
	}		

    /**
     * 
     * @param processo
     * @return Retorna a tarefa anterior do processo
     */
    public static Tarefa getTarefaAnterior(Processo processo) {
        Query query = EntityUtil.createQuery("select o.idPreviousTask from SituacaoProcesso o where o.idProcesso = :idProcesso");
        Integer idJbpmTaskAnterior = EntityUtil.getSingleResult(query.setParameter("idProcesso", processo.getIdProcesso()));
        TarefaManager tarefaManager = ComponentUtil.getComponent(TarefaManager.NAME);
        return idJbpmTaskAnterior != null ? tarefaManager.getTarefa(idJbpmTaskAnterior.longValue()) : null;
    }
	
	/**
	 * @param processo
	 * @return Retorna o nome da tarefa anterior no fluxo
	 */
	public String getNomeTarefaAnterior(Processo processo) {
		Tarefa tarefaAnterior = getTarefaAnterior(processo);
		return tarefaAnterior != null ? tarefaAnterior.getTarefa() : null;
	}
	
	/**
	 * @param processo
	 * @return Retorna o nome da tarefa anterior no fluxo
	 */
	@Factory(scope=ScopeType.EVENT, value=VAR_NOME_TAREFA_ANTERIOR)
	public String getNomeTarefaAnterior() {
		Processo processo = JbpmUtil.getProcesso();
		Tarefa tarefaAnterior = getTarefaAnterior(processo);
		return tarefaAnterior != null ? tarefaAnterior.getTarefa() : null;
	}	
	
	public boolean checkNomeTarefaAnterior(String... nomeTarefas) {
		String nomeTarefaAnterior = getNomeTarefaAnterior();
		for (String tarefa : nomeTarefas) {
			if (tarefa.equals(nomeTarefaAnterior)) {
				return true;
			}
		}
		return false;
	}
	
	public Transition getCurrentTransition() {
		ExecutionContext currentExecutionContext = ExecutionContext.currentExecutionContext();
		if (currentExecutionContext != null) {
			return currentExecutionContext.getTransition();
		} 
		return null;
	}
	
	/**
	 * Resume a instancia de uma tarefa e devolve o taskInstance da mesma.
	 * @param idTaskInstance
	 * @return
	 */
	public static TaskInstance resumeTask(Long idTaskInstance) {
		BusinessProcess.instance().setTaskId(idTaskInstance);
		BusinessProcess.instance().resumeTask(idTaskInstance);
		return org.jboss.seam.bpm.TaskInstance.instance();
	}
	
	/**
	 * Verifica se uam transição de destino está disponível
	 * @param taskInstance
	 * @param transitionDestino
	 * @return
	 */
	@SuppressWarnings(UNCHECKED)
	public static boolean canTransitTo(TaskInstance taskInstance, String transitionDestino) {
		List<Transition> availableTransitions = taskInstance.getAvailableTransitions();
		for (Transition transition : availableTransitions) {
			if (transition.getName().equals(transitionDestino)) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isTypeEditor(String type) {
		return type.startsWith("textEditCombo") || "textEditSignature".equals(type);
	}
	
}
