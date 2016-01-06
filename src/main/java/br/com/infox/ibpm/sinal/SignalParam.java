package br.com.infox.ibpm.sinal;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.el.impl.JbpmExpressionEvaluator;

public class SignalParam {

    private String name;
    private Object value;
    private String valueExpression;
    private Type type;
    
    public SignalParam(String name, String value, Type type) {
        this.name = name;
        if (value.startsWith("#") || value.startsWith("$")) {
            this.valueExpression = value;
        } else {
            this.value =  value;
        }
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(String valueExpression) {
        this.valueExpression = valueExpression;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
    
    public Object getParamValue() {
        return getParamValue(ExecutionContext.currentExecutionContext());
    }
    
    public Object getParamValue(ExecutionContext executionContext) {
        if (value != null) return value;
        return JbpmExpressionEvaluator.evaluate(valueExpression, executionContext);
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof SignalParam))
            return false;
        SignalParam other = (SignalParam) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public enum Type {

        VARIABLE("Variável"), METADADO("Metadado");

        private String label;

        private Type(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

}
