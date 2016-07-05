package br.com.infox.jbpm.graphic;

import org.jbpm.graph.exe.Token;


public class GraphImageBean {
    
    private String key;
    private Token token;
    private Type type;
    private Boolean isCurrent;
    
    public GraphImageBean(String key, Token token, Type type, Boolean isCurrent) {
        this.key = key;
        this.token = token;
        this.type = type;
        this.isCurrent = isCurrent;
    }

    public GraphImageBean(String key, Type type, Token token) {
        this(key, token, type, false);
    }

    public String getKey() {
        return key;
    }

    public Type getType() {
        return type;
    }

    public Token getToken() {
        return token;
    }

    public Boolean isCurrent() {
        return isCurrent;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((key == null) ? 0 : key.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof GraphImageBean))
            return false;
        GraphImageBean other = (GraphImageBean) obj;
        if (key == null) {
            if (other.key != null)
                return false;
        } else if (!key.equals(other.key))
            return false;
        return true;
    }

    public enum Type {
        NODE, TRANSITION;
    }
}
