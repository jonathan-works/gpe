package br.com.infox.ibpm.jbpm;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.taskmgmt.exe.TaskInstance;

final class TaskVariableResolver {
    
    private VariableAccess variableAccess;
    private String name;
    private String type;
    private Object value;
    private TaskInstance taskInstance;
    
    public TaskVariableResolver(VariableAccess variableAccess, TaskInstance taskInstance) {
        this.variableAccess = variableAccess;
        this.type = variableAccess.getMappedName().split(":")[0];
        this.name = variableAccess.getMappedName().split(":")[1];
        this.taskInstance = taskInstance;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
    
    public void resolveWhenMonetario(){
        if ("numberMoney".equals(type) && value != null) {
            String val = String.valueOf(value);
            try {
                value = Float.parseFloat(val);
            } catch (NumberFormatException e) {
                value = Float.parseFloat(val.replace(".", "")
                        .replace(",", "."));
            }
        }
    }
    
    public boolean isEditor(){
        return type.startsWith("textEditCombo") || type.equals("textEditSignature");
    }
    
    public String getLabel() {
        return JbpmUtil.instance().getMessages().get(name);
    }
    
    public Integer getIdDocumento(){
        if (taskInstance.getVariable(variableAccess.getMappedName()) != null) {
            return (Integer) taskInstance.getVariable(variableAccess.getMappedName());
        } else {
            return null;
        }
    }
}
