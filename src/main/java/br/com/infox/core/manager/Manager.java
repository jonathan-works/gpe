package br.com.infox.core.manager;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.LockModeType;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;

@Transactional
@Scope(ScopeType.STATELESS)
public abstract class Manager<D extends DAO<T>, T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private D dao;

    @SuppressWarnings(UNCHECKED)
    @PostConstruct
    public void init() {
        this.dao = (D) Component.getInstance(getDaoName());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T persist(T o) throws DAOException {
        return (T) dao.persist(o);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T update(T o) throws DAOException {
        return (T) dao.update(o);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T remove(T o) throws DAOException {
        return (T) dao.remove(o);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T removeWithoutFlush(T o) {
    	return (T) dao.removeWithoutFlush(o);
    }
    
    public T find(Object id) {
        return dao.find(id);
    }
    
    public List<T> findByIds(Collection<? extends Number> ids) {
        return dao.findByIds(ids);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void lock(T entity) {
    	getDao().lock(entity, LockModeType.PESSIMISTIC_READ);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T lock(T entity, LockModeType lockModeType) {
    	return getDao().lock(entity, lockModeType);
    }
    
    public List<T> findAll() {
        return dao.findAll();
    }

    public boolean contains(Object o) {
        return dao.contains(o);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public T merge(T o) throws DAOException {
        return dao.merge(o);
    }

    public void detach(T o) {
        dao.detach(o);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void clear() {
        dao.clear();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void flush() {
        dao.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void refresh(T o) {
        dao.refresh(o);
    }

    public <X> X getSingleResult(final String query,
            final Map<String, Object> params) {
        return dao.getSingleResult(query, params);
    }

    public <X> List<X> getResultList(final String query,
            final Map<String, Object> params) {
        return dao.getResultList(query, params);
    }

    protected D getDao() {
        return this.dao;
    }

    @SuppressWarnings(UNCHECKED)
    protected String getDaoName() {
        ParameterizedType superType = (ParameterizedType) getClass().getGenericSuperclass();
        Class<D> daoClass = (Class<D>) superType.getActualTypeArguments()[0];
        Name name = daoClass.getAnnotation(Name.class);
        return name.value();
    }
}
