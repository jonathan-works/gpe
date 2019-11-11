package br.com.infox.kernel.repository;


public interface Repository<E, K> {
	E create(E entity);
	E update(E entity);
	E delete(E entity);
	E getById(K id);
}
