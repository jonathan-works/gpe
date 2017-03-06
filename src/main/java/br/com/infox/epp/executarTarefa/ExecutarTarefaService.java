package br.com.infox.epp.executarTarefa;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.jboss.seam.bpm.Actor;
import org.jbpm.JbpmContext;
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
import br.com.infox.epp.processo.form.TaskFormData;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.service.VariaveisJbpmProcessosGerais;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
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
	
	public TaskInstance salvarTarefa(TaskFormData formData, TaskInstance taskInstance) {
		JbpmContext jbpmContext = JbpmContextProducer.getJbpmContext();
		taskInstance = jbpmContext.getTaskInstanceForUpdate(taskInstance.getId());
		formData.setTaskInstance(taskInstance);
		formData.update();
		return taskInstance;
	}
	
	public TaskInstance finalizarTarefa(Transition transition, TaskInstance taskInstance, TaskFormData formData){
		taskInstance = getJbpmContext().getTaskInstanceForUpdate(taskInstance.getId());
		formData.setTaskInstance(taskInstance);
		formData.update();
		if(transition.isConditionEnforced() && formData.validate()){
	        return taskInstance;		        
		}
		taskInstance.end(transition);
		atualizarBam(taskInstance);
		
		return findNextTaskInstance(taskInstance.getToken());
	}

    private TaskInstance findNextTaskInstance(Token token) {
        Node node = token.getNode();
		if(node.getNodeType() == NodeType.Task){
		    TypedQuery<TaskInstance> query = getEntityManager().createNamedQuery("TaskMgmtSession.findTaskInstancesByTokenId", TaskInstance.class);
		    query.setParameter("tokenId", token.getId());
		    List<TaskInstance> list =  query.getResultList();
		    return list.isEmpty() ? null : list.get(0);
		}else if(node.getNodeType() == NodeType.Fork){
		    Map<String, Token> children = token.getChildren();
		    for (Token child : children.values()) {
                return findNextTaskInstance(child);
            }
		}else if(node.getNodeType() == NodeType.ProcessState){
		    return findNextTaskInstance(token.getSubProcessInstance().getRootToken());
		}else if(node.getNodeType() == NodeType.Join){
		    return findNextTaskInstance(token.getParent());
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
        formData.setTaskInstance(taskInstance);
        taskInstance.setVariable(name, typedValue.getType().convertToModelValue(typedValue.getValue()));
	}

	public boolean verificaPermissaoTarefa(TaskInstance taskInstance, TipoProcesso tipoProcesso){
	    return situacaoProcessoDAO.canOpenTask(taskInstance.getId(), tipoProcesso, false);
	}
	
	public TaskInstance atribuirTarefa(TaskInstance taskInstance) {
	    taskInstance = getJbpmContext().getTaskInstanceForUpdate(taskInstance.getId());
	    getJbpmContext().getSession().buildLockRequest(LockOptions.READ).setLockMode(LockMode.PESSIMISTIC_FORCE_INCREMENT).lock(taskInstance);
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
        return taskInstance;
	}
	
	private JbpmContext getJbpmContext() {
	    return JbpmContextProducer.getJbpmContext();
	}
}
