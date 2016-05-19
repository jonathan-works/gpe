package br.com.infox.epp.processo.form.variable.value;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class PrimitiveValueType implements ValueType {
    
    protected String name;
    
    public PrimitiveValueType(String name) {
        this.name = name;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Object convertToModelValue(TypedValue propertyValue) {
        return propertyValue.getValue();
    }

    @Override
    public String convertToStringValue(TypedValue propertyValue) {
        return propertyValue.getValue() == null ? "" : propertyValue.getValue().toString();
    }
    
    public static class BooleanValueType extends PrimitiveValueType {

        public BooleanValueType() {
            super(Boolean.class.getSimpleName().toLowerCase());
        }
    }
    
    public static class DoubleValueType extends PrimitiveValueType {

        public DoubleValueType() {
            super(Double.class.getSimpleName().toLowerCase());
        }
    }
    
    public static class IntegerValueType extends PrimitiveValueType {

        public IntegerValueType() {
            super(Integer.class.getSimpleName().toLowerCase());
        }
    }
    
    public static class StringValueType extends PrimitiveValueType {

        public StringValueType() {
            super(String.class.getSimpleName().toLowerCase());
        }
    }
    
    public static class DateValueType extends PrimitiveValueType {
        
        public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        public DateValueType() {
            super(Date.class.getSimpleName().toLowerCase());
        }
        
        @Override
        public String convertToStringValue(TypedValue propertyValue) {
            return DATE_FORMAT.format(propertyValue.getValue());
        }
    }
    
}
