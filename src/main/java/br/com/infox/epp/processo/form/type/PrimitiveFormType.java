package br.com.infox.epp.processo.form.type;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.ibpm.variable.VariableDataHandler;
import br.com.infox.ibpm.variable.VariableMaxMinHandler;
import br.com.infox.ibpm.variable.VariableMaxMinHandler.MaxMinConfig;
import br.com.infox.ibpm.variable.VariableStringHandler;
import br.com.infox.ibpm.variable.VariableStringHandler.StringConfig;
import br.com.infox.ibpm.variable.type.ValidacaoDataEnum;
import br.com.infox.seam.exception.BusinessException;

public abstract class PrimitiveFormType implements FormType {
    
    protected String name;
    protected String path;
    protected ValueType valueType;
    
    public PrimitiveFormType(String name, String path, ValueType valueType) {
        this.name = name;
        this.path = path;
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
    public String getPath() {
        return path;
    }
    
    @Override
    public Object convertToFormValue(Object value) {
        return value;
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
       String required = formField.getProperty("required", String.class);
       if("true".equalsIgnoreCase(required) && formField.getValue() == null){
    	   FacesContext.getCurrentInstance().addMessage(formField.getUiComponent().getClientId(), new FacesMessage(FacesMessage.SEVERITY_ERROR, "", InfoxMessages.getInstance().get("beanValidation.notNull")));
       }
    }
    
    public static class StringFormType extends PrimitiveFormType {
        
        public StringFormType() {
            super("string", "/Processo/form/string.xhtml", ValueType.STRING);
        }
        
        public StringFormType(String name, String path) {
            super(name, path, ValueType.STRING);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
        	super.performValue(formField, formData);
        	String configuration = (String) formField.getProperties().get("configuration");
        	if(configuration != null) {
        		StringConfig config = VariableStringHandler.fromJson(configuration);
        		formField.addProperty("mascara", config.getMascara());
        	}
        }
    }
    
    public static class TextFormType extends StringFormType {
        
        public TextFormType() {
            super("text", "/Processo/form/text.xhtml");
        }
    }
    
    public static class StructuredTextFormType extends StringFormType {
        
        public StructuredTextFormType() {
            super("structuredText", "/Processo/form/structuredText.xhtml");
        }
    }
    
    public static class BooleanFormType extends PrimitiveFormType {
        
        public BooleanFormType() {
            super("boolean", "/Processo/form/boolean.xhtml", ValueType.BOOLEAN);
        }
        
        @Override
        public Object convertToFormValue(Object value) {
            if (value == null) {
                value = Boolean.FALSE;
            }
            return value;
        }
    }
    
    public static class IntegerFormType extends PrimitiveFormType {
        
        public IntegerFormType() {
            super("integer", "/Processo/form/integer.xhtml", ValueType.INTEGER);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
        	super.performValue(formField, formData);
        	String configuration = (String) formField.getProperties().get("configuration");
            if (configuration == null) {
            	MaxMinConfig maxMinConfig = VariableMaxMinHandler.fromJson(configuration);
            	formField.addProperty("valorMinimo", maxMinConfig.getMinimo());
            	formField.addProperty("valorMaximo", maxMinConfig.getMaximo());
            }
        }
    }
    
    public static class DateFormType extends PrimitiveFormType {
        
        public DateFormType() {
            super("date", "/Processo/form/date.xhtml", ValueType.DATE);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
            super.performValue(formField, formData);
            String configuration = (String) formField.getProperties().get("configuration");
            ValidacaoDataEnum validacaoData = null;
            if (configuration == null) {
                validacaoData = ValidacaoDataEnum.L;
            } else {
                validacaoData = VariableDataHandler.fromJson(configuration).getTipoValidacaoData();
            }
            formField.getProperties().put("validatorId", validacaoData.getValidatorId());
        }
        
    }
    
    public static class MonetaryFormType extends PrimitiveFormType {
        
        public MonetaryFormType() {
            super("monetary", "/Processo/form/monetary.xhtml", ValueType.DOUBLE);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
        	super.performValue(formField, formData);
        	String configuration = (String) formField.getProperties().get("configuration");
            if (configuration == null) {
            	MaxMinConfig maxMinConfig = VariableMaxMinHandler.fromJson(configuration);
            	formField.addProperty("valorMinimo", maxMinConfig.getMinimo());
            	formField.addProperty("valorMaximo", maxMinConfig.getMaximo());
            }
        }
    }
    
    public static class FrameFormType extends PrimitiveFormType {
        
        public FrameFormType() {
            super("frame", "/Processo/form/frame.xhtml", ValueType.NULL);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
            super.performValue(formField, formData);
            VariableDefinitionService variableDefinitionService = BeanManager.INSTANCE.getReference(VariableDefinitionService.class);
            FrameDefinition frame = variableDefinitionService.getFrame(formField.getId());
            String framePath = frame.getXhtmlPath();
            formField.getProperties().put("framePath", framePath);
        }

        @Override
        public boolean isPersistable() {
            return false;
        }
    }
    
    public static class PageFormType extends PrimitiveFormType {
        
        public PageFormType() {
            super("page", "/Processo/form/page.xhtml", ValueType.NULL);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
            super.performValue(formField, formData);
            String url = String.format("/%s.%s", formField.getId().replaceAll("_", "/"), "seam");
            formField.getProperties().put("url", url);
        }

        @Override
        public boolean isPersistable() {
            return false;
        }
    }
    
    public static class TaskPageFormType extends PrimitiveFormType {
        
        public TaskPageFormType() {
            super("taskPage", "", ValueType.NULL);
        }
        
        @Override
        public void performValue(FormField formField, FormData formData) {
            this.path = "/WEB-INF/taskpages/" + formField.getId() + ".xhtml";
        }

        @Override
        public boolean isPersistable() {
            return false;
        }
    }
    
}
