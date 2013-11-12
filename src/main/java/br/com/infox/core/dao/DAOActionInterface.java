package br.com.infox.core.dao;

public interface DAOActionInterface<T> {
    T execute(T obj);
}
