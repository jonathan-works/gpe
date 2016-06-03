package br.com.infox.epp.processo.form.variable.value;

public interface TypedValue {

    Object getValue();
    
    void setValue(Object object);
    
    ValueType getType();
}
