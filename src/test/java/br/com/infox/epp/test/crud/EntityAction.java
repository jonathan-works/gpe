package br.com.infox.epp.test.crud;

public interface EntityAction<T> {
    void run(T entity);
}
