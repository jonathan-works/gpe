package br.com.infox.epp.processo.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.hibernate.Session;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jbpm.JbpmContext;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.consulta.bean.MovimentacoesBean;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.ibpm.task.manager.UsuarioTaskInstanceManager;
import br.com.infox.seam.util.ComponentUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class MovimentacoesProcessoService implements Serializable {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
	private List<TaskInstance> getTaskInstanceList(Processo processo) {
        JbpmContext managedJbpmContext = ManagedJbpmContext.instance();
		org.jbpm.graph.exe.ProcessInstance processInstance = managedJbpmContext.getProcessInstance(processo.getIdJbpm());
		List<TaskInstance> taskInstanceList = new ArrayList<>(processInstance.getTaskMgmtInstance().getTaskInstances());

        Session session = managedJbpmContext.getSession();
        List<org.jbpm.graph.exe.ProcessInstance> subprocesses = session.getNamedQuery("GraphSession.findSubProcessInstances").setParameter("processInstance", processInstance).list();

        for (org.jbpm.graph.exe.ProcessInstance subprocess : subprocesses) {
            Collection<TaskInstance> subprocessTaskInstances = subprocess.getTaskMgmtInstance().getTaskInstances();
            if (subprocessTaskInstances != null) {
                taskInstanceList.addAll(subprocessTaskInstances);
            }
        }
        return taskInstanceList;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<MovimentacoesBean> getMovimentacoes(Processo processo) {
        List<TaskInstance> taskInstances = getTaskInstanceList(processo);
        List<MovimentacoesBean> movimentacoes = new ArrayList<>();
        ProcessoTarefaManager processoTarefaManager = ComponentUtil.getComponent(ProcessoTarefaManager.NAME);
        UsuarioTaskInstanceManager usuarioTaskInstanceManager = ComponentUtil.getComponent(UsuarioTaskInstanceManager.NAME);
        
        for (TaskInstance taskInstance : taskInstances) {
        	movimentacoes.add(new MovimentacoesBean(processoTarefaManager.getByTaskInstance(taskInstance.getId()), usuarioTaskInstanceManager.find(taskInstance.getId()), taskInstance));
        }
        
        Collections.sort(movimentacoes, new Comparator<MovimentacoesBean>() {
            @Override
            public int compare(MovimentacoesBean o1, MovimentacoesBean o2) {
                int result = o1.getDataInicio().compareTo(o2.getDataInicio());
                if (result == 0){
                    result = o1.getTarefa().getTarefa().compareToIgnoreCase(o2.getTarefa().getTarefa());
                }
                return result;
            }
        });
        return movimentacoes;
    }
}
