package br.com.infox.cdi.dao;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.config.BeanManager;

public abstract class Dao<T, I> {

	private Class<T> entityClass;

	public Dao(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	public T findById(I id) {
		return getEntityManager().find(entityClass, id);
	}

	public List<T> findAll() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);
		cq.from(entityClass);
		return getEntityManager().createQuery(cq).getResultList();
	}

	public T getSingleResult(TypedQuery<T> typedQuery) {
		List<T> result = typedQuery.setMaxResults(1).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}
	
	public EntityManager getEntityManager() {
		return BeanManager.INSTANCE.getReference(EntityManager.class);
	}
	
	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public void flush() throws DAOException {
		try {
			getEntityManager().flush();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T persist(T object) throws DAOException {
		try {
			getEntityManager().persist(object);
			getEntityManager().flush();
			return object;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T update(T object) throws DAOException {
		try {
			T res = getEntityManager().merge(object);
			getEntityManager().flush();
			return res;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T remove(T object) throws DAOException {
		try {
			if (!getEntityManager().contains(object)) {
				object = getEntityManager().merge(object);
			}
			getEntityManager().remove(object);
			getEntityManager().flush();
			return object;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	public void detach(T object) {
		getEntityManager().detach(object);
	}
	
	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T refresh(T object) throws DAOException {
		try {
			if (!getEntityManager().contains(object)) {
				object = getEntityManager().merge(object);
			}
			getEntityManager().refresh(object);
			getEntityManager().flush();
			return object;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
}
