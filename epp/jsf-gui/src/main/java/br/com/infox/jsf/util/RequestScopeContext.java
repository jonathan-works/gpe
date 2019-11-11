package br.com.infox.jsf.util;

import javax.faces.context.FacesContext;

public class RequestScopeContext {

    private RequestScopeContext() {
    }
    
    @SuppressWarnings("unchecked")
    public static <V> V get(String key){
        if (FacesContext.getCurrentInstance() == null)
            return null;
        
        return (V) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(key);
    }
    @SuppressWarnings("unchecked")
    public static <V> V put(String key, V value){
        if (FacesContext.getCurrentInstance() == null)
            return null;
        
        return (V) FacesContext.getCurrentInstance().getExternalContext().getRequestMap().put(key, value);
    }
    
}
