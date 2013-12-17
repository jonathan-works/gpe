package br.com.infox.epp.test.infra;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;
import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.persistence.DAOException;

public class MockGenericDAO extends GenericDAO {
    private static final long serialVersionUID = 1L;
    private HashMap<Object, Object> entities = new HashMap<>();

    @Override
    public boolean contains(Object o) {
        return entities.containsKey(getKey(o));
    }

    @SuppressWarnings(UNCHECKED)
    @Override
    public <T> T find(Class<T> c, Object id) {
        T object = (T) entities.get(id);
        return object;
    }

    @SuppressWarnings(UNCHECKED)
    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        final ArrayList<T> resultList = new ArrayList<>();
        for (Object object : entities.values()) {
            if (object.getClass().equals(clazz)) {
                resultList.add((T) object);
            }
        }
        return resultList;
    }

    @Override
    public <T> List<T> getResultList(String query,
            Map<String, Object> parameters) {
        return new ArrayList<>(0);
    }

    @Override
    public <T> T getSingleResult(String query, Map<String, Object> parameters) {
        return null;
    }

    @Override
    @SuppressWarnings(UNCHECKED)
    public <T> T merge(T object) throws DAOException {
        if (entities.containsValue(object)) {
            return (T) entities.put(getKey(object), object);
        }
        throw new DAOException();
    }

    @SuppressWarnings(UNCHECKED)
    @Override
    public <T> T persist(T object) throws DAOException {
        if (!entities.containsValue(object)) {
            return (T) entities.put(getKey(object), object);
        }
        throw new DAOException();
    }

    @SuppressWarnings(UNCHECKED)
    @Override
    public <T> T remove(T object) throws DAOException {
        if (entities.containsValue(object)) {
            return (T) entities.remove(getKey(object));
        }
        throw new DAOException();
    }

    @SuppressWarnings(UNCHECKED)
    @Override
    public <T> T update(T object) throws DAOException {
        if (entities.containsValue(object)) {
            return (T) entities.put(getKey(object), object);
        }
        throw new DAOException();
    }

    private <T> Object getKey(T object) {
        Object key=null;
        Method[] methods = object.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Id.class)) {
                try {
                    key = method.invoke(object);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                }
                break;
            }
        }
        return key;
    }

}
