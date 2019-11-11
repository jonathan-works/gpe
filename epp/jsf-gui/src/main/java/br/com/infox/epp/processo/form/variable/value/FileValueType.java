package br.com.infox.epp.processo.form.variable.value;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.processo.documento.entity.Documento;

public class FileValueType implements ValueType {
    
    @Override
    public String getName() {
        return "file";
    }
    
    @Override
    public Object convertToModelValue(Object value) {
        if (value == null) {
            return null;
        }
        if ((value instanceof String) && !StringUtil.isEmpty((String) value)) {
            return Integer.valueOf((String) value);
        }
        if (value instanceof Documento) {
            return ((Documento) value).getId();
        }
        throw new IllegalArgumentException("Impossible convert " + value);
    }
    
    @Override
    public String convertToStringValue(Object propertyValue) {
        Object value = convertToModelValue(propertyValue);
        return value == null ? null : value.toString();
    }
    
}
