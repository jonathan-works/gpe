package br.com.infox.epp.processo.form.variable.value;

import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

public abstract class PrimitiveTypedValue<T> implements TypedValue {

    protected T value;
    protected Class<T> javaType;
    protected ValueType type;

    public PrimitiveTypedValue(T value, Class<T> clazz, ValueType type) {
        this.javaType = clazz;
        this.value = value;
        this.type = type;
    }
    
    @Override
    public ValueType getType() {
        return type;
    }

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
    
    public static class NullValue extends PrimitiveTypedValue<Void>  {
        
        public static final NullValue INSTANCE = new NullValue();

        private NullValue() {
            super(null, null, ValueType.NULL);
        }
    }
    
    public static class BooleanValue extends PrimitiveTypedValue<Boolean>  {

        public BooleanValue(Boolean value) {
            super(value, Boolean.class, ValueType.BOOLEAN);
        }
    }

    public static class IntegerValue extends PrimitiveTypedValue<Integer>  {

        public IntegerValue(Integer value) {
            super(value, Integer.class, ValueType.INTEGER);
        }
    }

    public static class LongValue extends PrimitiveTypedValue<Long>  {

        public LongValue(Long value) {
            super(value, Long.class, ValueType.LONG);
        }
    }

    public static class DoubleValue extends PrimitiveTypedValue<Double> {

        public DoubleValue(Double value) {
            super(value, Double.class, ValueType.DOUBLE);
        }
    }

    public static class DateValue extends PrimitiveTypedValue<Date> {

        public DateValue(Date value) {
            super(value, Date.class, ValueType.DATE);
        }
    }
    
    public static class StringValue extends PrimitiveTypedValue<String> {

        public StringValue(String value) {
            super(value, String.class, ValueType.STRING);
        }
    }
    
    public static class StringArrayValue extends PrimitiveTypedValue<String[]> {

        public StringArrayValue(String[] value) {
            super(value, String[].class, ValueType.STRING_ARRAY);
        }
    }
    
    public static class EnumerationValue extends StringValue {
        
        protected List<SelectItem> selectItems;

        public EnumerationValue(String value) {
            super(value);
        }

        public List<SelectItem> getSelectItems() {
            return selectItems;
        }

        public void setSelectItems(List<SelectItem> selectItems) {
            this.selectItems = selectItems;
        }
    }
    
    public static class EnumerationMultipleValue extends StringArrayValue {
        
        protected List<SelectItem> selectItems;

        public EnumerationMultipleValue(String[] value) {
            super(value);
        }

        public List<SelectItem> getSelectItems() {
            return selectItems;
        }

        public void setSelectItems(List<SelectItem> selectItems) {
            this.selectItems = selectItems;
        }
    }
}
