package br.com.infox.epp.processo.form.type;

import java.text.ParseException;
import java.util.Date;

import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.PrimitiveTypedValue;
import br.com.infox.epp.processo.form.variable.value.PrimitiveTypedValue.NullValue;
import br.com.infox.epp.processo.form.variable.value.PrimitiveValueType;
import br.com.infox.epp.processo.form.variable.value.TypedValue;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.ibpm.variable.type.ValidacaoDataEnum;
import br.com.infox.seam.exception.BusinessException;

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
    public boolean isPersistable() {
        return true;
    }
    
    @Override
    public void performValue(FormField formField, FormData formData) {
        // do nothing
    }
    
    @Override
    public void performUpdate(FormField formField, FormData formData) {
       // do nothing
    }
    
    @Override
    public void validate(FormField formField, FormData formData) throws BusinessException {
       // do nothing
    }
    
    public static class StringFormType extends PrimitiveFormType {
        
        public StringFormType() {
            super("string", ValueType.STRING);
        }

        @Override
        public TypedValue convertToFormValue(Object value) {
            if (value == null) {
                return new PrimitiveTypedValue.StringValue(null);
            } else if (value instanceof String) {
                return new PrimitiveTypedValue.StringValue((String) value);
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
            return new PrimitiveTypedValue.BooleanValue((Boolean) value);
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
            return new PrimitiveTypedValue.IntegerValue((Integer) value);
        }
    }
    
    public static class DateFormType extends PrimitiveFormType {
        
        public DateFormType() {
            super("date", ValueType.DATE);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
            super.performValue(formField, formData);
            String extendedProperties = formField.getProperties().get("extendedProperties");
            ValidacaoDataEnum validacaoData = null;
            if (extendedProperties == null) {
                validacaoData = ValidacaoDataEnum.L;
            } else {
                validacaoData = ValidacaoDataEnum.valueOf(extendedProperties);
            }
            formField.getProperties().put("validatorId", validacaoData.getValidatorId());
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
            return new PrimitiveTypedValue.DateValue((Date) value);
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
            return new PrimitiveTypedValue.DoubleValue((Double) value);
        }
    }
    
    public static class FrameFormType extends PrimitiveFormType {
        
        public FrameFormType() {
            super("frame", ValueType.NULL);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
            super.performValue(formField, formData);
            String framePath = String.format("/%s.%s", formField.getId().replaceAll("_", "/"), "xhtml");
            formField.getProperties().put("framePath", framePath);
        }

        @Override
        public TypedValue convertToFormValue(Object value) {
            return NullValue.INSTANCE;
        }
        
        @Override
        public boolean isPersistable() {
            return false;
        }
    }
    
    public static class PageFormType extends PrimitiveFormType {
        
        public PageFormType() {
            super("page", ValueType.NULL);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
            super.performValue(formField, formData);
            String url = String.format("/%s.%s", formField.getId().replaceAll("_", "/"), "seam");
            formField.getProperties().put("url", url);
        }

        @Override
        public TypedValue convertToFormValue(Object value) {
            return NullValue.INSTANCE;
        }
        
        @Override
        public boolean isPersistable() {
            return false;
        }
    }
    
    public static class TaskPageFormType extends PrimitiveFormType {
        
        public TaskPageFormType() {
            super("taskPage", ValueType.NULL);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
            formField.setPath("/WEB-INF/taskpages/" + formField.getId() + ".xhtml");
        }

        @Override
        public TypedValue convertToFormValue(Object value) {
            return NullValue.INSTANCE;
        }
        
        @Override
        public boolean isPersistable() {
            return false;
        }
    }
    
}
