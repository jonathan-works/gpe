package br.com.infox.epp.processo.form;

import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;

import br.com.infox.epp.processo.form.type.FormType;

public class FormField {
    
    protected String id;
    protected String label;
    protected FormType type;
    protected Object defaultValue;
    protected Object value;
    protected Map<String, Object> properties = new HashMap<>();
    protected UIComponent component;

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
    
    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public <T> T getTypedValue(Class<T> type) {
        return type.cast(value);
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

	public UIComponent getComponent() {
		return component;
	}

	public void setComponent(UIComponent component) {
		this.component = component;
	}
	
	public boolean isRequired() {
	    return "true".equalsIgnoreCase(this.getProperty("required", String.class));
	}

}
