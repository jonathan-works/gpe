package br.com.infox.ibpm.jbpm;

import org.jbpm.context.def.VariableAccess;

import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

final class TaskVariable {
    
    private VariableAccess var;
    private String name;
    private String type;
    private Object variable;
    
    public TaskVariable (VariableAccess var){
        this.var = var;
        this.type = var.getMappedName().split(":")[0];
        this.name = var.getMappedName().split(":")[1];
    }

    public String getName() {
        return name;
    }
    
    public String getMappedName(){
        return var.getMappedName();
    }

    public String getType() {
        return type;
    }
    
    public boolean isEditor(){
        return type.startsWith("textEditCombo") || type.equals("textEditSignature");
    }
    
    public boolean isForm(){
        return "form".equals(type);
    }
    
    public boolean isWritable(){
        return var.isWritable();
    }
    
    public boolean isMonetario(){
        return "numberMoney".equals(type) && (variable != null)
                && (variable.getClass().equals(Float.class));
    }
    
    public void formatVariableMonetaria(){
        variable = String.format("%.2f", variable);
    }

    public Object getVariable() {
        return variable;
    }

    public void setVariable(Object variable) {
        this.variable = variable;
    }
    
    public boolean hasVariable(){
        return variable != null;
    }
    
    public void setVariablesHome(){
        AbstractHome<?> home = ComponentUtil.getComponent(getName() + "Home");
        home.setId(getVariable());
    }
    
    public void retrieveHomes(){
        if (hasVariable()){
            setVariablesHome();
        }
    }
    
}
