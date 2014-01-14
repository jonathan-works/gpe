package br.com.infox.core.dao;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.internal.SessionImpl;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.transaction.Transaction;

import br.com.infox.core.exception.ApplicationException;
import br.com.infox.core.persistence.DAOException;
//import br.com.itx.component.Util;
//import br.com.itx.util.EntityUtil;

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
	    if (id == null) {
	        return null;
	    }
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
	
    protected <T> List<T> getNamedResultList(final String namedQuery) {
        return getNamedResultList(namedQuery, null);
    }
	
	@SuppressWarnings(UNCHECKED)
	protected <T> List<T> getNamedResultList(final String namedQuery,
			final Map<String, Object> parameters) {
		Query q = getNamedQuery(namedQuery, parameters);
		return q.getResultList();
	}
	
	protected <T> T getNamedSingleResult(final String namedQuery){
	    return getNamedSingleResult(namedQuery, null);
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
	
	protected void executeNamedQueryUpdate(final String namedQuery){
        getNamedQuery(namedQuery, null).executeUpdate();
    }
	
	protected void executeNamedQueryUpdate(final String namedQuery, final Map<String, Object> parameters){
	    getNamedQuery(namedQuery, parameters).executeUpdate();
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
	        rollbackTransactionIfNeeded();
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
            rollbackTransactionIfNeeded();
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
            rollbackTransactionIfNeeded();
        }
	}
	
	public <T> T merge(final T object) throws DAOException {
	    try {
	        return entityManager.merge(object);
	    } catch (Exception e) {
            throw new DAOException(e);
        } finally {
            rollbackTransactionIfNeeded();
        }
	}

    protected EntityManager getEntityManager() {
        return entityManager;
    }
    
    public Query createQuery(final String query) {
        return createQuery(query, null);
    }
    
    public Query createQuery(final String query, final Map<String,Object> parameters) {
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
    
    public <T> void detach(T o) {
    	entityManager.detach(o);
    }

    public void clear() {
        entityManager.clear();
    }
    
    public void flush(){
        entityManager.flush();
    }
    
    public <T> void refresh(T o){
        entityManager.refresh(o);
    }
    
    public  void rollbackTransactionIfNeeded() {
        try {
            org.jboss.seam.transaction.UserTransaction ut = Transaction.instance();
            if(ut != null && ut.isMarkedRollback()) {
                SessionImpl session = entityManager.unwrap(SessionImpl.class);
                // Aborta o batch JDBC, possivelmente relacionado ao bug HHH-7689. Ver https://hibernate.atlassian.net/browse/HHH-7689
                session.getTransactionCoordinator().getJdbcCoordinator().abortBatch();
                ut.rollback();
            }
        } catch (Exception e) {
            throw new ApplicationException(ApplicationException.
                    createMessage("rollback da transação", 
                                  "rollbackTransaction()", 
                                  "Util", 
                                  "ePP"), e);
        }
    }

}