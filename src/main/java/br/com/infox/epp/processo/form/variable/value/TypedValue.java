package br.com.infox.epp.processo.form.variable.value;

public interface TypedValue {

    Object getValue();
    
    Class<?> getType();
}
