package br.com.infox.cdi.dao;

import java.io.Serializable;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.cdi.transaction.Transactional;

public class Dao<T, I> extends PersistenceController implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Class<T> entityClass;

	public Dao(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	public T findById(I id) {
		return getEntityManager().find(entityClass, id);
	}
	
	@Transactional
	public T selectById(I id) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<T> cq = cb.createQuery(entityClass);
	    Root<T> from = cq.from(entityClass);
	    cq.select(from);
	    EntityType<T> entity = getEntityManager().getMetamodel().entity(entityClass);
	    Type<?> idType = entity.getIdType();
	    SingularAttribute<? super T, ?> idSingularAttribute = entity.getId(idType.getJavaType());
	    cq.where(cb.equal(from.get(idSingularAttribute), cb.literal(id)));
        return getSingleResult(getEntityManager().createQuery(cq));
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
