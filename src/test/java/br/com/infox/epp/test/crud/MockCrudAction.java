package br.com.infox.epp.test.crud;

import java.util.List;

import br.com.infox.core.crud.Crudable;


public interface MockCrudAction<T> extends Crudable<T>{
    List<T> getAll();
    String inactive(T t);
    String remove(T entity);
}
