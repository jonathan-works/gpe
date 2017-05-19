package br.com.infox.epp.executarTarefa;

import java.util.Date;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.ws.Holder;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.jbpm.JbpmContext;
import org.jbpm.activity.exe.ParallelMultiInstanceActivityBehavior;
import org.jbpm.activity.exe.SequentialMultiInstanceActivityBehavior;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.JbpmContextProducer;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.access.manager.UsuarioLoginManager;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.TaskFormData;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.ibpm.task.dao.TaskInstanceSearch;
import br.com.infox.ibpm.task.entity.UsuarioTaskInstance;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
public class ExecutarTarefaService extends PersistenceController {

	@Inject
	private ProcessoTarefaManager processoTarefaManager;
	@Inject
    private SituacaoProcessoDAO situacaoProcessoDAO;
	@Inject
    private UsuarioLoginManager usuarioLoginManager;
	@Inject
	private TaskInstanceSearch taskInstanceSearch;
	
	public void salvarTarefa(TaskFormData formData, Holder<TaskInstance> taskInstance) {
		JbpmContext jbpmContext = JbpmContextProducer.getJbpmContext();
		taskInstance.value = jbpmContext.getTaskInstanceForUpdate(taskInstance.value.getId());
		formData.update();
	}
	
	public TaskInstance finalizarTarefa(Transition transition, Holder<TaskInstance> taskInstanceHolder, TaskFormData formData){
	    taskInstanceHolder.value = getJbpmContext().getTaskInstanceForUpdate(taskInstanceHolder.value.getId());
		formData.update();
		if(transition.isConditionEnforced() && formData.isInvalid()) {
	        return taskInstanceHolder.value;
		}
		taskInstanceHolder.value.end(transition);
		atualizarBam(taskInstanceHolder.value);
		return findNextTaskInstance(taskInstanceHolder.value.getToken());
	}

    private TaskInstance findNextTaskInstance(Token token) {
        Node node = token.getNode();
        node = HibernateUtil.removeProxy(node);
        if (node.getNodeType() == NodeType.Task) {
            TaskInstance newTaskInstance = taskInstanceSearch.findTaskInstanceByTokenId(token.getId());
            if ( newTaskInstance == null ) {
                TaskNode taskNode = (TaskNode) node;
                if ( ParallelMultiInstanceActivityBehavior.class.equals(taskNode.getActivityBehaviorClass()) 
                        || SequentialMultiInstanceActivityBehavior.class.equals(taskNode.getActivityBehaviorClass()) ) {
                    if ( token.hasActiveChildren() ) {
                        for (Token child : token.getActiveChildren().values()) {
                            return findNextTaskInstance(child);
                        }
                    } else {
                        return findNextTaskInstance(token.getParent());
                    }
                }
            }
            return newTaskInstance;
        } else if (node.getNodeType() == NodeType.Fork) {
            Map<String, Token> children = token.getChildren();
            for (Token child : children.values()) {
                return findNextTaskInstance(child);
            }
        } else if (node.getNodeType() == NodeType.ProcessState) {
            return findNextTaskInstance(token.getSubProcessInstance().getRootToken());
        } else if (node.getNodeType() == NodeType.Join) {
            Token parent = token.getParent();
            if (parent.getNode().getNodeType() != NodeType.Fork) {
                return findNextTaskInstance(parent);
            }
        } else if ( node.getNodeType() == NodeType.EndState ) {
            Token parentToken = token.getProcessInstance().getSuperProcessToken();
            if (parentToken != null && parentToken.getNode().getNodeType() != NodeType.ProcessState) {
                return findNextTaskInstance(parentToken);
            }
        }
        return null;
    }
    
	private void atualizarBam(TaskInstance taskInstance) {
		ProcessoTarefa pt = processoTarefaManager.getByTaskInstance(taskInstance.getId());
		Date dtFinalizacao = taskInstance.getEnd();
		pt.setDataFim(dtFinalizacao);
		processoTarefaManager.update(pt);
		processoTarefaManager.updateTempoGasto(dtFinalizacao, pt);
	}
	
	public void gravarUpload(String name, TypedValue typedValue, TaskFormData formData){
        TaskInstance taskInstance = getJbpmContext().getTaskInstanceForUpdate(formData.getTaskInstance().getId());
        taskInstance.setVariable(name, typedValue.getType().convertToModelValue(typedValue.getValue()));
	}

	public boolean verificaPermissaoTarefa(TaskInstance taskInstance, Processo processo){
	    MetadadoProcesso metadado = processo.getMetadado(EppMetadadoProvider.TIPO_PROCESSO);
	    return situacaoProcessoDAO.canOpenTask(taskInstance.getId(), metadado == null ? null : metadado.<TipoProcesso>getValue(), false);
	}
	
	public void atribuirTarefa(Holder<TaskInstance> taskInstanceHolder) {
	    taskInstanceHolder.value = getJbpmContext().getTaskInstanceForUpdate(taskInstanceHolder.value.getId());
	    getJbpmContext().getSession().buildLockRequest(LockOptions.READ).setLockMode(LockMode.PESSIMISTIC_FORCE_INCREMENT).lock(taskInstanceHolder.value);
	    TaskInstance taskInstance = taskInstanceHolder.value;
	    String currentActorId = Authenticator.getUsuarioLogado().getLogin();
        if (taskInstance.getStart() == null) {
            taskInstance.start(currentActorId);
            taskInstance.setAssignee(currentActorId);
        } else if (!StringUtil.isEmpty(taskInstance.getAssignee()) && !currentActorId.equals(taskInstance.getAssignee())) {
            throw new BusinessRollbackException("Tarefa bloqueada por outro usuário");
        } else {
            taskInstance.setAssignee(currentActorId);
        }
        UsuarioLogin usuario = usuarioLoginManager.getUsuarioLoginByLogin(currentActorId);
        taskInstance.setVariableLocally(VariaveisJbpmProcessosGerais.OWNER, usuario.getNomeUsuario());
        if (getEntityManager().find(UsuarioTaskInstance.class, taskInstance.getId()) == null) {
            getEntityManager().persist(new UsuarioTaskInstance(taskInstance.getId(), Authenticator.getUsuarioPerfilAtual()));
        }
	}
	
	private JbpmContext getJbpmContext() {
	    return JbpmContextProducer.getJbpmContext();
	}
}
