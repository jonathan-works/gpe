package br.com.infox.core.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.LockModeType;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.util.EntityUtil;

@Stateless
@AutoCreate
@Name(GenericDAO.NAME)
public class GenericDAO extends DAO<Object> {

    public static final String NAME = "genericDAO";
    private static final long serialVersionUID = 2513102779632819212L;
    private static final String MSG_INVALID_METHOD = "Operação inválida no GenericDAO";

    @Override
    public Object find(Object id) {
        throw new UnsupportedOperationException(MSG_INVALID_METHOD);
    }

    @Override
    public List<Object> findAll() {
        throw new UnsupportedOperationException(MSG_INVALID_METHOD);
    }

    @Override
    public Object getReference(Object primaryKey) {
        throw new UnsupportedOperationException(MSG_INVALID_METHOD);
    }

    public <T> T find(Class<T> entityClass, Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public <T> List<T> findAll(Class<T> entityClass) {
        final StringBuilder sb = new StringBuilder();
        sb.append("select o from ").append(entityClass.getName()).append(" o");
        return getEntityManager().createQuery(sb.toString(), entityClass).getResultList();
    }

    public <T> T getReference(Class<T> entityClass, Object id) {
        return getEntityManager().getReference(entityClass, id);
    }
    
    @Transactional
    public void lock(Object entity) {
        if (!EntityUtil.isEntity(entity)) {
            throw new IllegalArgumentException("Object is not an entity");
        }
        getEntityManager().lock(entity, LockModeType.PESSIMISTIC_READ);
    }
}
