package br.com.infox.ibpm.task.view;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Template implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    private Map<String, Object> properties = new LinkedHashMap<String, Object>();

    public String getId() {
        return id == null ? "default" : id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
