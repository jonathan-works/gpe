package br.com.infox.core.manager;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.core.persistence.DAOException;

public abstract class Manager<D extends DAO<T>, T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private D dao;

    @SuppressWarnings(UNCHECKED)
    @Create
    public void init() {
        this.dao = (D) Component.getInstance(getDaoName());
    }

    public T persist(T o) throws DAOException {
        return (T) dao.persist(o);
    }

    public T update(T o) throws DAOException {
        return (T) dao.update(o);
    }

    public T remove(T o) throws DAOException {
        return (T) dao.remove(o);
    }

    public T find(Object id) {
        return dao.find(id);
    }

    public List<T> findAll() {
        return dao.findAll();
    }

    public boolean contains(Object o) {
        return dao.contains(o);
    }

    public T merge(T o) throws DAOException {
        return dao.merge(o);
    }

    public void detach(T o) {
        dao.detach(o);
    }

    public void clear() {
        dao.clear();
    }

    public void flush() {
        dao.flush();
    }

    public void refresh(T o) {
        dao.refresh(o);
    }

    public Long getSingleResult(final String query,
            final Map<String, Object> params) {
        return (Long) dao.getSingleResult(query, params);
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
