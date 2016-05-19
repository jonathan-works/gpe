package br.com.infox.epp.processo.form.variable.value;

import java.util.Date;

public interface PrimitiveTypedValue<T> extends TypedValue {
    
    T getValue();
    
    Class<T> getJavaType();
    
    public static interface BooleanTypedValue extends PrimitiveTypedValue<Boolean> {
    }
    
    public static interface StringTypedValue extends PrimitiveTypedValue<String> {
    }
    
    public static interface LongTypedValue extends PrimitiveTypedValue<Long> {
    }
    
    public static interface IntegerTypedValue extends PrimitiveTypedValue<Integer> {
    }
    
    public static interface DoubleTypedValue extends PrimitiveTypedValue<Double> {
    }
    
    public static interface DateTypedValue extends PrimitiveTypedValue<Date> {
    }
}
