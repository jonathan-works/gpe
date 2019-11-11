package br.com.infox.epp.processo.form.variable.value;

public final class ValueTypes {
    
    public static ValueType create(String name) {
        for (ValueType valueType : ValueType.TYPES) {
            if (valueType.getName().equals(name)) {
                return valueType;
            }
        }
        throw new IllegalArgumentException("Not found value type for '" + name + "'");
    }
}
