package br.com.infox.epp.test.crud;

public interface ICrudActions<E> {
    void setEntityValue(final String field, final Object value);
    <R> R getEntityValue(final String field);
    void setComponentValue(final String field, final Object value);
    <R> R getComponentValue(final String field);
    Object invokeMethod(final String methodName, final Object... args);
    <R> R invokeMethod(final String methodName, final Class<R> returnType, final Class<?>[] paramTypes, final Object...args);
    <R> R invokeMethod(final String methodName, final Class<R> returnType, final Object... args);
    void newInstance();
    E createInstance();
    E resetInstance(Object id);
    E getInstance();
    void setInstance(final E value);
    String save();
    String remove();
    String remove(final E entity);
    String inactivate();
    Integer getId();
    void setId(Object value);
}