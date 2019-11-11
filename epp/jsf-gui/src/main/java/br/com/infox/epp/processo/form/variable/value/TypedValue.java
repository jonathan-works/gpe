package br.com.infox.epp.processo.form.variable.value;

public class TypedValue {
    
    protected Object value;
    protected ValueType type;
    
    public TypedValue(Object value, ValueType type) {
        this.value = value;
        this.type = type;
    }

    public Object getValue() {
        return value;
    }
    
    public <T> T getValue(Class<T> type) {
        return type.cast(value);
    }
    
    public void setValue(Object value) {
        this.value = value;
    }

    public ValueType getType() {
        return type;
    }

    public void setType(ValueType type) {
        this.type = type;
    }
    
}
