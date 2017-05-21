package br.com.infox.ibpm.variable.components;

import javax.xml.ws.Holder;

import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.form.TaskFormData;

public abstract class AbstractTaskPageController implements TaskpageController {
    
    private Holder<TaskInstance> taskInstanceHolder;
    private Holder<Processo> processoHolder;
    private TaskpageDefinition taskpageDefinition;
    
    @Override
    public void initialize(Holder<TaskInstance> taskInstanceHolder, Holder<Processo> processo, TaskpageDefinition taskpageDefinition) {
       this.taskInstanceHolder = taskInstanceHolder;
       this.processoHolder = processo;
       this.taskpageDefinition = taskpageDefinition;
       initialize();
    }
    
    protected void initialize() {
    }
    
    @Override
    public void preFinalizarTarefa(Transition transition, TaskFormData formData) {
    }
    
    @Override
    public void finalizarTarefa(Transition transition, TaskFormData formData) {
        
    }
    
    protected TaskInstance getTaskInstance() {
        return taskInstanceHolder == null ? null : taskInstanceHolder.value;
    }
    
    protected Processo getProcesso() {
        return processoHolder == null ? null : processoHolder.value;
    }
    
    public TaskpageDefinition getTaskpageDefinition() {
        return taskpageDefinition;
    }
    
    public Object getVariable(String variableName){
    	if( !StringUtil.isEmpty(variableName) ) {
    	    return getTaskInstance().getContextInstance().getVariable(variableName);
    	}
    	return null;
    }
    
    public <T> T getVariable(String variableName, Class<T> returnType){
        Object variable = getTaskInstance().getContextInstance().getVariable(variableName);
        return returnType.cast(variable);
    }
    
    public void setVariable(String variableName, Object value){
    	if( !StringUtil.isEmpty(variableName) ) {
    	    getTaskInstance().getContextInstance().setVariable(variableName,value);
    	}
    }
    
    public void setVariable(String variableName, Object value, Token token){
        if( !StringUtil.isEmpty(variableName) ) {
            getTaskInstance().getContextInstance().setVariable(variableName, value, token);
        }
    }
    
    @Override
    public boolean canCompleteTask() {
        return true;
    }
    
    @Override
    public String getIdFormButtons() {
        return "idFormButtons";
    }
    
    
}
