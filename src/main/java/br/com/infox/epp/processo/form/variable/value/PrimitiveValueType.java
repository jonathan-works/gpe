package br.com.infox.epp.processo.form.variable.value;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
    public TypedValue convertToModelValue(TypedValue propertyValue) {
        return propertyValue;
    }

    @Override
    public String convertToStringValue(TypedValue propertyValue) {
        return propertyValue.getValue() == null ? "" : propertyValue.getValue().toString();
    }
    
    public static class NullValueType extends PrimitiveValueType {
        
        public NullValueType() {
            super("null");
        }
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
    
    public static class LongValueType extends PrimitiveValueType {

        public LongValueType() {
            super(Long.class.getSimpleName().toLowerCase());
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
    
    public static class StringArrayValueType extends PrimitiveValueType {
        
        private static final Gson GSON = new GsonBuilder().create();
        
        public StringArrayValueType() {
            super("stringArray");
        }
        
        @Override
        public TypedValue convertToModelValue(TypedValue propertyValue) {
            Object object = propertyValue.getValue();
            if (object == null) {
                return new PrimitiveTypedValue.StringArrayValue(null);
            }
            if (object instanceof String) {
                String[] array = GSON.fromJson((String) object, String[].class);
                return new PrimitiveTypedValue.StringArrayValue(array);
            } 
            if (object instanceof String[]) {
                return new PrimitiveTypedValue.StringArrayValue((String[]) object);
            }
            throw new IllegalArgumentException("Cannot convert '" + object + "' to String[]");
        }

        @Override
        public String convertToStringValue(TypedValue propertyValue) {
            Object object = propertyValue.getValue();
            if (object == null) {
                return null;
            }
            if (object instanceof String) {
                return (String) object;
            }
            if (object instanceof String[]) {
                return GSON.toJson(object);
            }
            throw new IllegalArgumentException("Cannot convert '" + object + "' to String");
        }
        
    }
    
}
