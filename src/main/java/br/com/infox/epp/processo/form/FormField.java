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
    protected TypedValue typedValue;
    protected Map<String, Object> properties = new HashMap<>();

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

    public void setType(FormType type) {
        this.type = type;
    }

    public String getTypeName() {
        return type.getName();
    }
    
    public String getPath() {
        return type.getPath();
    }
    
    public TypedValue getDefaultValue() {
        return defaultValue;
    }

       public void setDefaultValue(TypedValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    public TypedValue getTypedValue() {
        return typedValue;
    }

    public void setTypedValue(TypedValue typedValue) {
        this.typedValue = typedValue;
    }
    
    public <T> T getValue(Class<T> type) {
        return type.cast(typedValue.getValue());
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
    
    public <T> T getProperty(String key, Class<T> type) {
        return type.cast(properties.get(key));
    }

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }
}
