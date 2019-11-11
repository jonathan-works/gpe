package br.com.infox.ibpm.task.view;

import java.io.Serializable;
import java.util.Map;

import org.jboss.seam.core.Expressions;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.ibpm.process.definition.variable.VariableType;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public class FormField implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(FormField.class);

    private String id;

    private Form form;

    private String formId;

    private String formHome;

    private String label;

    private String type;

    private String valueExpression;

    private String required;

    private String rendered = "true";

    private Map<String, Object> properties = new PropertyMap<String, Object>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        if (label == null) {
            String key = id;
            if (key.indexOf('.') > -1) {
                key = key.substring(0, key.indexOf('.'));
            }
            key = getFormId() + "." + key;
            if (InfoxMessages.getInstance().containsKey(key)) {
                label = InfoxMessages.getInstance().get(key);
            } else {
                label = label != null ? label : id;
            }
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getType() {
        if (type == null || VariableType.NULL.name().equals(type)) {
            return VariableType.STRING.name();
        } else {
            return type;
        }
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValueExpression() {
        return valueExpression;
    }

    public void setValueExpression(String valueExpression) {
        this.valueExpression = valueExpression;
    }

    public Object getValue() {
        Expressions exp = Expressions.instance();
        return exp.createValueExpression(getVEString()).getValue();
    }

    public void setValue(Object obj) {
        Expressions exp = Expressions.instance();
        exp.createValueExpression(getVEString()).setValue(obj);
    }

    private String getVEString() {
        if (valueExpression == null) {
            if (formHome != null) {
                return "#{" + getFormHome() + ".instance." + id + "}";
            } else {
                return "#{" + getFormId() + "Form.home.instance." + id + "}";
            }
        } else if (valueExpression.startsWith("\\#{")) {
            return valueExpression.substring(1);
        } else {
            return "#{" + valueExpression + "}";
        }
    }

    public boolean isRequired() {
        Boolean value = Boolean.FALSE;
        try {
            Expressions exp = Expressions.instance();
            value = (Boolean) exp.createValueExpression("#{" + required + "}").getValue();
            if (value == null) {
                value = Boolean.FALSE;
            }
        } catch (RuntimeException e) {
            LOG.error("isRequired()", e);
        }
        return value.booleanValue();
    }

    public void setRequired(String required) {
        this.required = required;
    }

    public boolean isRendered() {
        Boolean value = Boolean.FALSE;
        try {
            Expressions exp = Expressions.instance();
            value = (Boolean) exp.createValueExpression("#{" + rendered + "}").getValue();
            if (value == null) {
                value = Boolean.TRUE;
            }
        } catch (RuntimeException e) {
            LOG.error("isRendered()", e);
        }
        return value.booleanValue();
    }

    public void setRendered(String rendered) {
        this.rendered = rendered;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties.clear();
        this.properties.putAll(properties);
    }

    public String getFormId() {
        if (form != null) {
            formId = form.getFormId();
        }
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormHome() {
        return formHome;
    }

    public void setFormHome(String formHome) {
        this.formHome = formHome;
    }

}
