package br.com.infox.epp.processo.form.variable.value;

import java.util.Date;

public class PrimitiveTypedValueImpl<T> implements PrimitiveTypedValue<T> {

    protected T value;
    protected Class<T> javaType;
    protected ValueType type;

    public PrimitiveTypedValueImpl(T value, Class<T> clazz, ValueType type) {
        this.javaType = clazz;
        this.value = value;
        this.type = type;
    }
    
    @Override
    public ValueType getType() {
        return type;
    }

    @Override
    public Class<T> getJavaType() {
        return javaType;
    }

    @Override
    public T getValue() {
        return value;
    }
    
    public void setValue(T value) {
        this.value = value;
    }
    
    public static class BooleanValue extends PrimitiveTypedValueImpl<Boolean> implements BooleanTypedValue {

        public BooleanValue(Boolean value) {
            super(value, Boolean.class, null);
        }
    }

    public static class IntegerValue extends PrimitiveTypedValueImpl<Integer> implements IntegerTypedValue {

        public IntegerValue(Integer value) {
            super(value, Integer.class, null);
        }
    }

    public static class LongValue extends PrimitiveTypedValueImpl<Long> implements LongTypedValue {

        public LongValue(Long value) {
            super(value, Long.class, null);
        }
    }

    public static class DoubleValue extends PrimitiveTypedValueImpl<Double> implements DoubleTypedValue {

        public DoubleValue(Double value) {
            super(value, Double.class, null);
        }
    }

    public static class DateValue extends PrimitiveTypedValueImpl<Date> implements DateTypedValue {

        public DateValue(Date value) {
            super(value, Date.class, null);
        }
    }
    
    public static class StringValue extends PrimitiveTypedValueImpl<String> implements StringTypedValue {

        public StringValue(String value) {
            super(value, String.class, ValueType.STRING);
        }
    }
}
