package br.com.infox.core.dao;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.util.EntityUtil;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cdi.seam.ContextDependency;

@AutoCreate
@Transactional
@Scope(ScopeType.STATELESS)
@ContextDependency
public abstract class DAO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Busca o registro na entidade informada.
     * 
     * @param <T>
     * @param c Entidade
     * @param id do registro
     * @return objeto encontrado.
     */
    public T find(Object id) {
        if (id == null) {
            return null;
        }
        Class<T> entityClass = getEntityClass();
        return getEntityManager().find(entityClass, id);
    }
    
    public Object getIdentifier(T entity) {
    	EntityManagerFactory emf = getEntityManager().getEntityManagerFactory();
    	return emf.getPersistenceUnitUtil().getIdentifier(entity);
    }
    
    public List<T> findByIds(Collection<? extends Number> ids) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<T> criteriaQuery = cb.createQuery(getEntityClass());
    	Root<T> root = criteriaQuery.from(getEntityClass());
    	criteriaQuery.select(root);
    	String idAttributeName = EntityUtil.getId(getEntityClass()).getName();
    	criteriaQuery.where(root.get(idAttributeName).in(ids));
    	return getEntityManager().createQuery(criteriaQuery).getResultList();
    }
    
    public T lock(T entity, LockModeType lockModeType) {
    	if (!getEntityManager().contains(entity)) {
    		entity = getEntityManager().find(getEntityClass(), getIdentifier(entity));
    	}
    	getEntityManager().lock(entity, lockModeType);
    	return entity;
    }
    
    @SuppressWarnings(UNCHECKED)
    protected Class<T> getEntityClass() {
        ParameterizedType superType = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) superType.getActualTypeArguments()[0];
    }
    
    public <K>  K getSingleResult(TypedQuery<K> typedQuery) {
        List<K> result = typedQuery.setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Verifica se o entityManager contém o objeto informado.
     * 
     * @param o objeto a ser verificado.
     * @return true se contiver.
     */
    public boolean contains(final Object o) {
        return getEntityManager().contains(o);
    }

    /**
     * Obtém todos os registros da entidade informada.
     * 
     * @param <E>
     * @param clazz entidade
     * @return lista de todos os registros da entidade
     */
    public List<T> findAll() {
        Class<T> clazz = getEntityClass();
        final StringBuilder sb = new StringBuilder();
        sb.append("select o from ").append(clazz.getName()).append(" o");
        return getEntityManager().createQuery(sb.toString(), clazz).getResultList();
    }

    protected <X> List<X> getNamedResultList(final String namedQuery) {
        return getNamedResultList(namedQuery, null);
    }

    @SuppressWarnings(UNCHECKED)
    protected <X> List<X> getNamedResultList(final String namedQuery,
            final Map<String, Object> parameters) {
        Query q = getNamedQuery(namedQuery, parameters);
        return q.getResultList();
    }

    protected <X> X getNamedSingleResult(final String namedQuery) {
        return getNamedSingleResult(namedQuery, null);
    }

    @SuppressWarnings(UNCHECKED)
    protected <X> X getNamedSingleResult(final String namedQuery,
            final Map<String, Object> parameters) {
        Query q = getNamedQuery(namedQuery, parameters).setMaxResults(1);
        List<X> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    protected Query getNamedQuery(final String namedQuery,
            final Map<String, Object> parameters) {
        final Query q = getEntityManager().createNamedQuery(namedQuery);
        if (parameters != null) {
            for (Entry<String, Object> e : parameters.entrySet()) {
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        return q;
    }

    protected void executeNamedQueryUpdate(final String namedQuery) throws DAOException {
        try {
            getNamedQuery(namedQuery, null).executeUpdate();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    protected void executeNamedQueryUpdate(final String namedQuery,
            final Map<String, Object> parameters) throws DAOException {
        try {
            getNamedQuery(namedQuery, parameters).executeUpdate();
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Transactional
    public T persist(final T object) throws DAOException {
        try {
            getEntityManager().persist(object);
            getEntityManager().flush();
            return object;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Transactional
    public T update(final T object) throws DAOException {
        try {
            final T res = getEntityManager().merge(object);
            getEntityManager().flush();
            return res;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Transactional
    public T remove(final T object) throws DAOException {
        try {
            getEntityManager().remove(object);
            getEntityManager().flush();
            return object;
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    @Transactional
    public T removeWithoutFlush(final T object) {
		getEntityManager().remove(object);
		return object;
    }

    public T merge(final T object) throws DAOException {
        try {
            return getEntityManager().merge(object);
        } catch (Exception e) {
            throw new DAOException(e);
        }
    }

    public EntityManager getEntityManager() {
    	return BeanManager.INSTANCE.getReference(EntityManager.class);
    }

    // TODO: Ajeitar trees que chamam isso
    public Query createQuery(final String query) {
        return createQuery(query, null);
    }

    public T getReference(Object primaryKey) {
        return getEntityManager().getReference(getEntityClass(), primaryKey);
    }

    // TODO: Ajeitar trees que chamam isso
    public Query createQuery(final String query,
            final Map<String, Object> parameters) {
        final Query q = getEntityManager().createQuery(query);
        if (parameters != null) {
            for (Entry<String, Object> e : parameters.entrySet()) {
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        return q;
    }

    @SuppressWarnings(UNCHECKED)
    public <X> List<X> getResultList(final String query,
            final Map<String, Object> parameters) {
        return createQuery(query, parameters).getResultList();
    }
    
    @SuppressWarnings(UNCHECKED)
    public <X> List<X> getResultList(final String query,
    		final Map<String, Object> parameters, int first, int maxResult) {
    	return createQuery(query, parameters).setFirstResult(first).setMaxResults(maxResult).getResultList();
    }
    

    @SuppressWarnings(UNCHECKED)
    public <X> X getSingleResult(final String query,
            final Map<String, Object> parameters) {
        final Query q = createQuery(query, parameters).setMaxResults(1);
        final List<X> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public void detach(T o) {
        getEntityManager().detach(o);
    }

    public void clear() {
        getEntityManager().clear();
    }

    @Transactional
    public void flush() {
        getEntityManager().flush();
    }

    public void refresh(T o) {
        getEntityManager().refresh(o);
    }

    protected Query createNativeQuery(String nativeQuery, Map<String, Object> parameters) {
        final Query query = getEntityManager().createNativeQuery(nativeQuery);
        if (parameters != null) {
            for (Entry<String, Object> e : parameters.entrySet()) {
                query.setParameter(e.getKey(), e.getValue());
            }
        }
        return query;
    }
}
