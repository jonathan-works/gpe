package br.com.infox.epp.processo.form.type;

import java.text.ParseException;
import java.util.Date;

import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.PrimitiveTypedValueImpl;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;

public abstract class PrimitiveFormType implements FormType {
    
    protected String name;
    protected ValueType valueType;
    
    public PrimitiveFormType(String name, ValueType valueType) {
        this.name = name;
        this.valueType = valueType;
    }
    
    @Override
    public ValueType getValueType() {
        return valueType;
    }

    @Override
    public String getName() {
        return name;
    }
    
    @Override
    public void performValue(FormField formField, FormData formData) {
        // do nothing
    }
    
    @Override
    public void performUpdate(FormField formField, FormData formData) {
       // do nothing
    }
    
    public static class StringFormType extends PrimitiveFormType {
        
        public StringFormType() {
            super("string", ValueType.STRING);
        }

        @Override
        public TypedValue convertToFormValue(Object value) {
            if (value == null) {
                return new PrimitiveTypedValueImpl.StringValue(null);
            } else if (value instanceof String) {
                return new PrimitiveTypedValueImpl.StringValue((String) value);
            }
            throw new IllegalArgumentException("Object " + value + " cannot be converted");
        }
    }
    
    public static class TextFormType extends StringFormType {
        
        @Override
        public String getName() {
            return "text";
        }
    }
    
    public static class StructuredTextFormType extends StringFormType {
        
        @Override
        public String getName() {
            return "structuredText";
        }
    }
     
    public static class BooleanFormType extends PrimitiveFormType {
        
        public BooleanFormType() {
            super("boolean", ValueType.BOOLEAN);
        }
        
        @Override
        public TypedValue convertToFormValue(Object value) {
            if (value == null) {
                value = Boolean.FALSE;
            } else if (value instanceof String) {
                value = Boolean.valueOf((String) value);
            }
            return new PrimitiveTypedValueImpl.BooleanValue((Boolean) value);
        }
    }
    
    public static class IntegerFormType extends PrimitiveFormType {
        
        public IntegerFormType() {
            super("integer", ValueType.INTEGER);
        }

        @Override
        public TypedValue convertToFormValue(Object value) {
            if (value != null && (value instanceof String)) {
                value = Integer.valueOf((String) value);
            }
            return new PrimitiveTypedValueImpl.IntegerValue((Integer) value);
        }
    }
    
    public static class DateFormType extends PrimitiveFormType {
        
        public DateFormType() {
            super("date", ValueType.DATE);
        }
        
        @Override
        public TypedValue convertToFormValue(Object value) {
            if (value != null && (value instanceof String)) {
                try {
                    value = PrimitiveValueType.DateValueType.DATE_FORMAT.parse((String) value);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Invalid dateFormat " + value);
                }
            }
            return new PrimitiveTypedValueImpl.DateValue((Date) value);
        }
    }
    
    public static class MonetaryFormType extends PrimitiveFormType {
        
        public MonetaryFormType() {
            super("monetary", ValueType.DOUBLE);
        }
        
        @Override
        public TypedValue convertToFormValue(Object value) {
            if (value != null && (value instanceof String)) {
                value = Double.valueOf((String) value);
            }
            return new PrimitiveTypedValueImpl.DoubleValue((Double) value);
        }
    }

}
