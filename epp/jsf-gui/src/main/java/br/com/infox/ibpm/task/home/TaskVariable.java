package br.com.infox.ibpm.task.home;

import static br.com.infox.ibpm.process.definition.variable.VariableType.MONETARY;

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
        this.type = VariableType.valueOf(variableAccess.getType());
        this.name = variableAccess.getVariableName();
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
        return !variableAccess.isReadable() && variableAccess.isWritable();
    }

    public boolean isEditor() {
        return VariableType.EDITOR.equals(type);
    }
    
    public boolean isFile() {
        return VariableType.FILE.equals(type);
    }

    public boolean isVariableType(VariableType varType) {
        return varType.equals(type);
    }

    public boolean isWritable() {
        return variableAccess.isWritable();
    }

    public boolean isMonetario() {
        return MONETARY.equals(type);
    }

}
