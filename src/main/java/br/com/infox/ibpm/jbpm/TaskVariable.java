package br.com.infox.ibpm.jbpm;

import org.jbpm.context.def.VariableAccess;

final class TaskVariable {
    
    private String name;
    private String type;
    
    public TaskVariable (VariableAccess var){
        this.type = var.getMappedName().split(":")[0];
        this.name = var.getMappedName().split(":")[1];
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

}
