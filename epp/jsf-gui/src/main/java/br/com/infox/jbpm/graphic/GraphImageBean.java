package br.com.infox.jbpm.graphic;

import org.jbpm.graph.exe.Token;

public abstract class GraphImageBean {
    
    private String key;
    private Token token;
    
    public GraphImageBean(String key, Token token) {
        this.key = key;
        this.token = token;
    }

    public String getKey() {
        return key;
    }

    public Token getToken() {
        return token;
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
    
}
