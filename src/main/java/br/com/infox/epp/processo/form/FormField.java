package br.com.infox.epp.processo.form;

import java.util.HashMap;
import java.util.Map;

import br.com.infox.epp.processo.form.type.FormType;
import br.com.infox.epp.processo.form.variable.value.TypedValue;

public class FormField {
    
    protected String id;
    protected String label;
    protected FormType type;
    protected TypedValue defaultValue;
    protected TypedValue value;
    protected Map<String, String> properties = new HashMap<String, String>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public FormType getType() {
        return type;
    }

    public String getTypeName() {
        return type.getName();
    }

    public void setType(FormType type) {
        this.type = type;
    }

    public TypedValue getDefaultValue() {
        return defaultValue;
    }

    public TypedValue getValue() {
        return value;
    }

    public void setDefaultValue(TypedValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void setValue(TypedValue value) {
        this.value = value;
    }

    public Map<String, String> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void addProperties(String key, String value) {
        properties.put(key, value);
    }
}
