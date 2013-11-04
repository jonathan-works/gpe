package br.com.infox.ibpm.jbpm;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

abstract class TaskVariable {
    
    protected VariableAccess variableAccess;
    protected String name;
    protected String type;
    protected TaskInstance taskInstance;
    
    public TaskVariable (VariableAccess variableAccess, TaskInstance taskInstance){
        this.variableAccess = variableAccess;
        this.type = variableAccess.getMappedName().split(":")[0];
        this.name = variableAccess.getMappedName().split(":")[1];
        this.taskInstance = taskInstance;
    }
    
    public String getName() {
        return name;
    }
    
    public String getMappedName(){
        return variableAccess.getMappedName();
    }

    public String getType() {
        return type;
    }

}
