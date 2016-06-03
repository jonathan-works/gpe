package br.com.infox.epp.processo.form.variable.value;

import br.com.infox.epp.processo.documento.entity.Documento;

public class FileValueType implements ValueType {
    
    @Override
    public String getName() {
        return "file";
    }
    
    @Override
    public TypedValue convertToModelValue(TypedValue propertyValue) {
        Object value = propertyValue.getValue();
        if (value == null) {
            return new PrimitiveTypedValue.IntegerValue(null);
        }
        if (value instanceof Documento) {
            return new PrimitiveTypedValue.IntegerValue(((Documento) value).getId());
        }
        throw new IllegalArgumentException("Impossible convert " + propertyValue);
    }
    
    @Override
    public String convertToStringValue(TypedValue propertyValue) {
        TypedValue typedValue = convertToModelValue(propertyValue);
        return typedValue.getValue() == null ? null : typedValue.getValue().toString();
    }
    
}
