package br.com.infox.core.manager;

public interface EntityListener<T> {
    
    @SuppressWarnings("rawtypes")
    Class<? extends AbstractEntityListener> getServiceClass();

}
