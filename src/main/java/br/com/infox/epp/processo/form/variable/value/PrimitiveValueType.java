package br.com.infox.epp.processo.form.variable.value;

import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class PrimitiveValueType implements ValueType {
    
    protected String name;
    protected Class<?> javaType;
    
    public PrimitiveValueType(String name, Class<?> javaType) {
        this.name = name;
        this.javaType = javaType;
    }
    
    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public Object convertToModelValue(Object propertyValue) {
        if (propertyValue == null) return null;
        if ((propertyValue instanceof String) && javaType != null) {
            try {
                Constructor<?> constructor = javaType.getConstructor(String.class);
                if (constructor != null) {
                    return constructor.newInstance(propertyValue);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot convert " + propertyValue + " to " + javaType.getName());
            }
        }
        return propertyValue;
    }
    
    @Override
    public String convertToStringValue(Object propertyValue) {
        return propertyValue == null ? null : propertyValue.toString();
    }
    
    public static class NullValueType extends PrimitiveValueType {

        public NullValueType() {
            super("null", null);
        }
        
    }
    
    public static class BooleanValueType extends PrimitiveValueType {

        public BooleanValueType() {
            super(Boolean.class.getSimpleName().toLowerCase(), Boolean.class);
        }
        
    }
    
    public static class DoubleValueType extends PrimitiveValueType {

        public DoubleValueType() {
            super(Double.class.getSimpleName().toLowerCase(), Double.class);
        }
    }
    
    public static class IntegerValueType extends PrimitiveValueType {

        public IntegerValueType() {
            super(Integer.class.getSimpleName().toLowerCase(), Integer.class);
        }
    }
    
    public static class LongValueType extends PrimitiveValueType {

        public LongValueType() {
            super(Long.class.getSimpleName().toLowerCase(), Long.class);
        }
    }
    
    public static class StringValueType extends PrimitiveValueType {

        public StringValueType() {
            super(String.class.getSimpleName().toLowerCase(), String.class);
        }
    }
    
    public static class ParameterValueType extends PrimitiveValueType {

        public ParameterValueType() {
            super("parameter", String.class);
        }
    }
    
    public static class DateValueType extends PrimitiveValueType {
        
        public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        public DateValueType() {
            super(Date.class.getSimpleName().toLowerCase(), Date.class);
        }
        
        @Override
        public Object convertToModelValue(Object value) {
            if (value != null) {
                return null;
            }
            if (value instanceof String) {
                try {
                    return DATE_FORMAT.parse((String) value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Cannot parse " + value + " to Date");
                }
            }
            if (value instanceof Date) {
                return value;
            }
            throw new IllegalArgumentException("Cannot parse " + value + " to Date");
        }
        
        @Override
        public String convertToStringValue(Object propertyValue) {
            return DATE_FORMAT.format(propertyValue);
        }
    }
    
    public static class StringArrayValueType extends PrimitiveValueType {
        
        private static final Gson GSON = new GsonBuilder().create();
        
        public StringArrayValueType() {
            super("stringArray", String[].class);
        }
        
        @Override
        public Object convertToModelValue(Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                String[] array = GSON.fromJson((String) value, String[].class);
                return array;
            } 
            if (value instanceof String[]) {
                return value;
            }
            throw new IllegalArgumentException("Cannot convert '" + value + "' to String[]");
        }

        @Override
        public String convertToStringValue(Object value) {
            if (value == null) {
                return null;
            }
            if (value instanceof String) {
                return (String) value;
            }
            if (value instanceof String[]) {
                return GSON.toJson(value);
            }
            throw new IllegalArgumentException("Cannot convert '" + value + "' to String");
        }
    }
}
