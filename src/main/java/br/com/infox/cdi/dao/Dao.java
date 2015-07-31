package br.com.infox.cdi.dao;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import br.com.infox.core.persistence.DAOException;

public abstract class Dao<T, I> {

	@Inject
	protected EntityManager entityManager;

	private Class<T> entityClass;

	public Dao(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	public T findById(I id) {
		return entityManager.find(entityClass, id);
	}

	public List<T> findAll() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);
		cq.from(entityClass);
		return entityManager.createQuery(cq).getResultList();
	}

	public T getSingleResult(TypedQuery<T> typedQuery) {
		List<T> result = typedQuery.setMaxResults(1).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public void flush() throws DAOException {
		try {
			entityManager.flush();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T persist(T object) throws DAOException {
		try {
			entityManager.persist(object);
			entityManager.flush();
			return object;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T update(T object) throws DAOException {
		try {
			T res = entityManager.merge(object);
			entityManager.flush();
			return res;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T remove(T object) throws DAOException {
		try {
			if (!entityManager.contains(object)) {
				object = entityManager.merge(object);
			}
			entityManager.remove(object);
			entityManager.flush();
			return object;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	public void detach(T object) {
		entityManager.detach(object);
	}
	
	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T refresh(T object) throws DAOException {
		try {
			if (!entityManager.contains(object)) {
				object = entityManager.merge(object);
			}
			entityManager.refresh(object);
			entityManager.flush();
			return object;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
}
