package br.com.infox.epp.processo.form.type;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.ibpm.variable.VariableDataHandler;
import br.com.infox.ibpm.variable.VariableMaxMinHandler;
import br.com.infox.ibpm.variable.VariableMaxMinHandler.MaxMinConfig;
import br.com.infox.ibpm.variable.VariableStringHandler;
import br.com.infox.ibpm.variable.VariableStringHandler.StringConfig;
import br.com.infox.ibpm.variable.components.FrameDefinition;
import br.com.infox.ibpm.variable.components.VariableDefinitionService;
import br.com.infox.ibpm.variable.type.ValidacaoDataEnum;

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
    public boolean isInvalid(FormField formField, FormData formData) {
        if (formField.isRequired() && formField.getValue() == null) {
            FacesContext.getCurrentInstance().addMessage(formField.getComponent().getClientId(), new FacesMessage(
                    FacesMessage.SEVERITY_ERROR, "", InfoxMessages.getInstance().get("beanValidation.notNull")));
            return true;
        }
        return false;
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
            if (configuration != null) {
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
                //value = Boolean.FALSE;
            }
            return value;
        }
    }

    public static class IntegerFormType extends NumberFormType {

        public IntegerFormType() {
            super("integer", "/Processo/form/integer.xhtml", ValueType.INTEGER);
        }

        @Override
        public void performValue(FormField formField, FormData formData) {
            super.performValue(formField, formData);
            MaxMinConfig maxMinConfig = getConfigurator(formField);
            if(maxMinConfig != null){
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

    public static class MonetaryFormType extends NumberFormType {

        public MonetaryFormType() {
            super("monetary", "/Processo/form/monetary.xhtml", ValueType.DOUBLE);
        }

        @Override
        public void performValue(FormField formField, FormData formData) {
            super.performValue(formField, formData);
            MaxMinConfig maxMinConfig = getConfigurator(formField);
            if(maxMinConfig != null){
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
            VariableDefinitionService variableDefinitionService = Beans.getReference(VariableDefinitionService.class);
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

    public static class NumberFormType extends PrimitiveFormType {

        public NumberFormType(String name, String path, ValueType valueType) {
            super(name, path, valueType);
        }
        
        public MaxMinConfig getConfigurator(FormField formField) {
            String configuration = (String) formField.getProperties().get("configuration");
            MaxMinConfig maxMinConfig = VariableMaxMinHandler.fromJson(configuration);
            return maxMinConfig;
        }
        
        @Override
        public boolean isInvalid(FormField formField, FormData formData) {
            return super.isInvalid(formField, formData) || validateMaxMin(formField);
        }

        private boolean validateMaxMin(FormField formField) {
            MaxMinConfig maxMinConfig = getConfigurator(formField);
            String msg = null;
            if (formField.getValue() != null && maxMinConfig != null) {
                double valor = Double.parseDouble(formField.getValue().toString());
                if (maxMinConfig.getMinimo() != null && maxMinConfig.getMaximo() != null
                        && (valor < maxMinConfig.getMinimo().longValue()
                                || valor > maxMinConfig.getMaximo().longValue())) {
                    msg = InfoxMessages.getInstance().getFormated("beanValidation.sizeValue", maxMinConfig.getMinimo(), maxMinConfig.getMaximo());

                    FacesContext.getCurrentInstance().addMessage(formField.getComponent().getClientId(),
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg));
                } else if (maxMinConfig.getMaximo() != null && valor > maxMinConfig.getMaximo().longValue()) {
                    msg = InfoxMessages.getInstance().getFormated("beanValidation.menorOuIgual", maxMinConfig.getMaximo());

                    FacesContext.getCurrentInstance().addMessage(formField.getComponent().getClientId(),
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg));
                } else if (maxMinConfig.getMinimo() != null && valor < maxMinConfig.getMinimo().longValue()) {
                    msg = InfoxMessages.getInstance().getFormated("beanValidation.maiorOuIgual", maxMinConfig.getMinimo());

                    FacesContext.getCurrentInstance().addMessage(formField.getComponent().getClientId(),
                            new FacesMessage(FacesMessage.SEVERITY_ERROR, "", msg));
                }
            }
            return msg != null;
        }
        
    }
    
}
