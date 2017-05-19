package br.com.infox.ibpm.variable.components;

import javax.xml.ws.Holder;

import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.TaskFormData;

public abstract class AbstractTaskPageController implements TaskpageController {
    
    private Holder<TaskInstance> taskInstanceHolder;
    private Holder<Processo> processoHolder;
    
    @Override
    public void initialize(Holder<TaskInstance> taskInstanceHolder, Holder<Processo> processo) {
       this.taskInstanceHolder = taskInstanceHolder;
       this.processoHolder = processo;
       initialize();
    }
    
    protected void initialize() {
    }
    
    @Override
    public void finalizarTarefa(Transition transition, TaskFormData formData) {
        // TODO Auto-generated method stub
    }
    
    protected TaskInstance getTaskInstance() {
        return taskInstanceHolder.value;
    }
    
    protected Processo getProcesso() {
        return processoHolder.value;
    }
    
    public Object getVariable(String variableName){
    	if(variableName != null && !variableName.trim().isEmpty())
    		getTaskInstance().getContextInstance().getVariable(variableName);
    	return null;
    }

}
