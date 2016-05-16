package br.com.infox.epp.processo.form;

import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.epp.processo.entity.Processo;

public class TaskFormDataImpl extends AbstractFormData implements TaskFormData {
    
    protected TaskInstance taskInstance;
    
    public TaskFormDataImpl(Processo processo, TaskInstance taskInstance) {
        super(processo);
        this.taskInstance = taskInstance;
    }

    @Override
    public TaskInstance getTaskInstance() {
        return taskInstance;
    }

    @Override
    public Object getVariable(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void putVariable(String name, Object value) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object evaluate(String expression) {
        // TODO Auto-generated method stub
        return null;
    }

}
