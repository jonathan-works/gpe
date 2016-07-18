package br.com.infox.cdi.dao;

import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PersistenceController;

public class Dao<T, I> extends PersistenceController {

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

	public <K>  K getSingleResult(TypedQuery<K> typedQuery) {
		List<K> result = typedQuery.setMaxResults(1).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}
	
	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public void flush() {
		try {
			getEntityManager().flush();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T persist(T object) {
		try {
			getEntityManager().persist(object);
			getEntityManager().flush();
			return object;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T update(T object) {
		try {
			T res = getEntityManager().merge(object);
			getEntityManager().flush();
			return res;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T remove(T object) {
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
	public T refresh(T object) {
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
	
	@TransactionAttribute(TransactionAttributeType.MANDATORY)
	public T lock(T object, LockModeType lock) {
		if (!getEntityManager().contains(object)) {
			object = getEntityManager().find(entityClass, getIdentifier(object));
    	}
		getEntityManager().lock(object, lock);
		return object;
	}
	
    public Object getIdentifier(T entity) {
    	EntityManagerFactory emf = getEntityManager().getEntityManagerFactory();
    	return emf.getPersistenceUnitUtil().getIdentifier(entity);
    }
}
