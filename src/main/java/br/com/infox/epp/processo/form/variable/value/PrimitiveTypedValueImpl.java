package br.com.infox.epp.processo.form.variable.value;

import java.util.Date;


public class PrimitiveTypedValueImpl<T> implements PrimitiveTypedValue<T> {

    protected T value;
    protected Class<T> clazz;

    public PrimitiveTypedValueImpl(T value, Class<T> clazz) {
        this.clazz = clazz;
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }
    
    @Override
    public Class<T> getType() {
        return clazz;
    }
    
    public static class BooleanValue extends PrimitiveTypedValueImpl<Boolean> implements BooleanTypedValue {

        public BooleanValue(Boolean value) {
            super(value, Boolean.class);
        }
    }

    public static class IntegerValue extends PrimitiveTypedValueImpl<Integer>  {

        public IntegerValue(Integer value) {
            super(value, Integer.class);
        }
    }

    public static class LongValue extends PrimitiveTypedValueImpl<Long> {

        public LongValue(Long value) {
            super(value, Long.class);
        }
    }

    public static class DoubleValue extends PrimitiveTypedValueImpl<Double> {

        public DoubleValue(Double value) {
            super(value, Double.class);
        }
    }

    public static class DateValue extends PrimitiveTypedValueImpl<Date> {

        public DateValue(Date value) {
            super(value, Date.class);
        }
    }
    
    public static class StringValue extends PrimitiveTypedValueImpl<String> {

        public StringValue(String value) {
            super(value, String.class);
        }
    }

}
