package br.com.infox.core.dao;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.persistence.DAOException;
import br.com.itx.component.Util;

/**
 * DAO generico para consultas, persistencia
 * entre outros.
 * @author Daniel
 *
 */
@Name(GenericDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class GenericDAO implements Serializable {

    private static final long serialVersionUID = 2513102779632819212L;

	public static final String NAME = "genericDAO";
	
	@In
	private transient EntityManager entityManager;
	
	/**
	 * Busca o registro na entidade informada.
	 * @param <T> 
	 * @param c Entidade
	 * @param id do registro
	 * @return objeto encontrado.
	 */
	public <T> T find(final Class<T> c, final Object id) {
		return entityManager.find(c, id);
	}
	
	/**
	 * Verifica se o entityManager contém o objeto
	 * informado.
	 * @param o objeto a ser verificado.
	 * @return true se contiver.
	 */
	public boolean contains(final Object o) {
		return entityManager.contains(o);
	}
	
	/**
	 * Obtém todos os registros da entidade informada.
	 * @param <E>
	 * @param clazz entidade
	 * @return lista de todos os registros da entidade
	 */
	@SuppressWarnings(UNCHECKED)
	public <T> List<T> findAll(final Class<T> clazz) {
		final StringBuilder sb = new StringBuilder();
		sb.append("select o from ").append(clazz.getName()).append(" o");
		return entityManager.createQuery(sb.toString()).getResultList();
	}
	
	@SuppressWarnings(UNCHECKED)
	protected <T> List<T> getNamedResultList(final String namedQuery,
			final Map<String, Object> parameters) {
		Query q = getNamedQuery(namedQuery, parameters);
		return q.getResultList();
	}

	@SuppressWarnings(UNCHECKED)
	protected <T> T getNamedSingleResult(final String namedQuery,
			final Map<String, Object> parameters) {
		Query q = getNamedQuery(namedQuery, parameters)
		        .setMaxResults(1);
        List<T> list = q.getResultList();
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
	}

	protected Query getNamedQuery(final String namedQuery,
			final Map<String, Object> parameters) {
		final Query q = entityManager.createNamedQuery(namedQuery);
		if(parameters != null) {
			for (Entry<String, Object> e : parameters.entrySet()) {
				q.setParameter(e.getKey(), e.getValue());
			}
		}
		return q;
	}
	
	@Transactional
	public <T> T persist(final T object) throws DAOException{
	    try {
	        entityManager.persist(object);
            entityManager.flush();
            return object;
	    } catch (Exception e) {
	        throw new DAOException(e);
	    } finally {
	        Util.rollbackTransactionIfNeeded();
	    }
	}

    @Transactional
	public <T> T update(final T object) throws DAOException{
        try {
            final T res = entityManager.merge(object);
            entityManager.flush();
            return res;
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            Util.rollbackTransactionIfNeeded();
        }
	}
	
    @Transactional
	public <T> T remove(final T object) throws DAOException{
	    try {
            entityManager.remove(object);
            entityManager.flush();
            return object;
    	} catch (Exception e) {
            throw new DAOException(e);
        } finally {
            Util.rollbackTransactionIfNeeded();
        }
	}
	
	public <T> T merge(final T object) throws DAOException {
	    try {
	        return entityManager.merge(object);
	    } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            Util.rollbackTransactionIfNeeded();
        }
	}

    protected EntityManager getEntityManager() {
        return entityManager;
    }
    
    protected Query createQuery(final String query, final Map<String,Object> parameters) {
        final Query q = entityManager.createQuery(query);
        if(parameters != null) {
            for (Entry<String, Object> e : parameters.entrySet()) {
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        return q;
    }
 
    @SuppressWarnings(UNCHECKED)
    public <T> List<T> getResultList(final String query, final Map<String,Object> parameters) {
        return createQuery(query, parameters).getResultList();
    }
    
    @SuppressWarnings(UNCHECKED)
    public <T> T getSingleResult(final String query, final Map<String,Object> parameters) {
        final Query q = createQuery(query, parameters).setMaxResults(1);
        final List<T> list = q.getResultList();
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }
    
}