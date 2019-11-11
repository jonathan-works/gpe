package br.com.infox.epp.test.crud;

public interface CrudActions<E> {
    E createInstance();
    <R> R getComponentValue(final String field);
    <R> R getEntityValue(final String field);
    Integer getId();
    E getInstance();
    String inactivate();
    Object invokeMethod(final String methodName);
    <R> R invokeMethod(final String methodName, final Class<R> returnType, final Class<?>[] paramTypes, final Object...args);
    <R> R invokeMethod(final String methodName, final Class<R> returnType, final Object... args);
    Object invokeMethod(final String componentName, final String methodName);
    <R> R invokeMethod(final String componentName, final String methodName, final Class<R> returnType, final Class<?>[] paramTypes, final Object...args);
    <R> R invokeMethod(final String componentName, final String methodName, final Class<R> returnType, final Object... args);
    void newInstance();
    String remove();
    String remove(final E entity);
    E resetInstance(Object id);
    String save();
    void setComponentValue(final String field, final Object value);
    void setEntityValue(final String field, final Object value);
    void setId(Object value);
    void setInstance(final E value);
}