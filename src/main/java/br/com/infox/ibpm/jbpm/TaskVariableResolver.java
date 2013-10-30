package br.com.infox.ibpm.jbpm;

import org.jbpm.context.def.VariableAccess;

final class TaskVariableResolver {
    
    private VariableAccess variableAccess;
    private String name;
    private String type;
    private Object value;
    
    public TaskVariableResolver(VariableAccess variableAccess) {
        this.variableAccess = variableAccess;
        this.type = variableAccess.getMappedName().split(":")[0];
        this.name = variableAccess.getMappedName().split(":")[1];
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

}
