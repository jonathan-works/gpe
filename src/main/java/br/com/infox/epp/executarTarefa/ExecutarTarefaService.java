package br.com.infox.epp.executarTarefa;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.jbpm.JbpmContext;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.producer.JbpmContextProducer;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.form.TaskFormData;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;

@Stateless
public class ExecutarTarefaService extends PersistenceController {

	@Inject
	private ProcessoTarefaManager processoTarefaManager;
	
	public TaskInstance salvarTarefa(TaskFormData formData, TaskInstance taskInstance) {
		JbpmContext jbpmContext = JbpmContextProducer.getJbpmContext();
		taskInstance = jbpmContext.getTaskInstanceForUpdate(taskInstance.getId());
		formData.setTaskInstance(taskInstance);
		formData.update();
		return taskInstance;
	}
	
	public TaskInstance finalizarTarefa(Transition transition, TaskInstance taskInstance, TaskFormData formData){
		JbpmContext jbpmContext = JbpmContextProducer.getJbpmContext();
		taskInstance = jbpmContext.getTaskInstanceForUpdate(taskInstance.getId());
		formData.setTaskInstance(taskInstance);
		formData.update();
		if(transition.isConditionEnforced() && formData.validate()){
	        return taskInstance;		        
		}
		taskInstance.end(transition);
		atualizarBam(taskInstance);
		return taskInstance;
	}
	
	private void atualizarBam(TaskInstance taskInstance) {
		ProcessoTarefa pt = processoTarefaManager.getByTaskInstance(taskInstance.getId());
		Date dtFinalizacao = taskInstance.getEnd();
		pt.setDataFim(dtFinalizacao);
		processoTarefaManager.update(pt);
		processoTarefaManager.updateTempoGasto(dtFinalizacao, pt);
	}
	
	public void gravarUpload(String name, TypedValue typedValue, TaskFormData formData){
	    JbpmContext jbpmContext = JbpmContextProducer.getJbpmContext();
        TaskInstance taskInstance = jbpmContext.getTaskInstanceForUpdate(formData.getTaskInstance().getId());
        formData.setTaskInstance(taskInstance);
        taskInstance.setVariable(name, typedValue.getType().convertToModelValue(typedValue.getValue()));
	}
}
