package br.com.infox.epp.processo.form.variable.value;

public class ArrayValueType implements ValueType {
    
    @Override
    public String getName() {
        return "array";
    }

    @Override
    public TypedValue convertToModelValue(TypedValue propertyValue) {
        Object value = propertyValue.getValue();
        if (value != null && value instanceof String) {
            
        }
        return null;
    }

    @Override
    public String convertToStringValue(TypedValue propertyValue) {
        return null;
    }

}
