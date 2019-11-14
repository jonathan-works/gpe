package br.com.infox.kernel.manager;

public interface CrudManager<T> {
	T create(T entity);
	T update(T entity);
	T delete(T entity);
	void inactive(T entity);
}