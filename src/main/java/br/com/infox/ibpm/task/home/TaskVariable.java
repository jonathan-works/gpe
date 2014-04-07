package br.com.infox.ibpm.task.home;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.ibpm.process.definition.variable.VariableType;

abstract class TaskVariable {

    protected VariableAccess variableAccess;
    protected String name;
    protected VariableType type;
    protected TaskInstance taskInstance;

    public TaskVariable(VariableAccess variableAccess, TaskInstance taskInstance) {
        this.variableAccess = variableAccess;
        this.type = VariableType.valueOf(variableAccess.getMappedName().split(":")[0]);
        this.name = variableAccess.getMappedName().split(":")[1];
        this.taskInstance = taskInstance;
    }

    public String getName() {
        return name;
    }

    public String getMappedName() {
        return variableAccess.getMappedName();
    }

    public VariableType getType() {
        return type;
    }
    
    public boolean isHidden() {
        return variableAccess.getAccess().hasAccess("hidden");
    }

}
