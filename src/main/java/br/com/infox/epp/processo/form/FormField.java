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
    protected String path;
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

    public void setType(FormType type) {
        this.type = type;
    }

    public String getTypeName() {
        return type.getName();
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
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
