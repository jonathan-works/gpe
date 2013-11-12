package br.com.infox.core.dao;

import static br.com.itx.util.EntityUtil.getSingleResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

import br.com.infox.util.constants.WarningConstants;
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
	private static final LogProvider LOG = Logging.getLogProvider(GenericDAO.class);

	public static final String NAME = "genericDAO";
	
	@In
	protected transient EntityManager entityManager;
	
	/**
	 * Busca o registro na entidade informada.
	 * @param <T> 
	 * @param c Entidade
	 * @param id do registro
	 * @return objeto encontrado.
	 */
	public <T> T find(Class<T> c, Object id) {
		return entityManager.find(c, id);
	}
	
	/**
	 * Verifica se o entityManager contém o objeto
	 * informado.
	 * @param o objeto a ser verificado.
	 * @return true se contiver.
	 */
	public boolean contains(Object o) {
		return entityManager.contains(o);
	}
	
	/**
	 * Obtém todos os registros da entidade informada.
	 * @param <E>
	 * @param clazz entidade
	 * @return lista de todos os registros da entidade
	 */
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public <T> List<T> findAll(Class<T> clazz) {
		StringBuilder sb = new StringBuilder();
		sb.append("select o from ").append(clazz.getName()).append(" o");
		return (List<T>) entityManager.createQuery(sb.toString()).getResultList();
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	protected <T> List<T> getNamedResultList(String namedQuery,
			Map<String, Object> parameters) {
		Query q = getNamedQuery(namedQuery, parameters);
		return (List<T>) q.getResultList();
	}

	@SuppressWarnings(WarningConstants.UNCHECKED)
	protected <T> T getNamedSingleResult(String namedQuery,
			Map<String, Object> parameters) {
		Query q = getNamedQuery(namedQuery, parameters);
		return (T) getSingleResult(q);
	}

	protected Query getNamedQuery(String namedQuery,
			Map<String, Object> parameters) {
		Query q = entityManager.createNamedQuery(namedQuery);
		if(parameters != null) {
			for (Entry<String, Object> e : parameters.entrySet()) {
				q.setParameter(e.getKey(), e.getValue());
			}
		}
		return q;
	}
	
	public <T> T persist(T object){
	    final T result = processExceptions(new DAOActionInterface<T>() {
            @Override
            @Transactional
            public T execute(final T obj) {
                entityManager.persist(obj);
                entityManager.flush();
                return obj;
            }
        },object,".persist() (" + object.getClass().getName() + ")");
	    
        return result;
	}
	
	public <T> T update(T object){
	    final T result = processExceptions(new DAOActionInterface<T>() {
            @Override
            @Transactional
            public T execute(final T obj) {
                final T res = entityManager.merge(obj);
                entityManager.flush();
                return res;
            }
        },object,".update() (" + object.getClass().getName() + ")");
	    
        return result;
	}
	
	public <T> T remove(final T object){
	    final T result = processExceptions(new DAOActionInterface<T>() {
            @Override
            @Transactional
            public T execute(T obj) {
                entityManager.remove(obj);
                entityManager.flush();
                return obj;
            }
        },object,".remove() (" + object.getClass().getName() + ")");
	    
	    return result;
	}
	
	private <T> T processExceptions(DAOActionInterface<T> action,T object,String msg) {
	    StopWatch sw = new StopWatch();
        sw.start();
        T ret = null;
	    try {
	        ret = action.execute(object);
	    }catch(IllegalArgumentException e) {
	        LOG.error(msg,e);
	    }catch(EntityExistsException e) {
	        LOG.error(msg,e);
	    }catch(TransactionRequiredException e) {    
	        LOG.error(msg,e);
	    }catch(PersistenceException e) {
	        LOG.error(msg,e);
	    }catch(Exception e) {
	        LOG.error(msg,e);
	    }
	    if (ret == null) {
	        Util.rollbackTransactionIfNeeded();
	    }
	    return ret;
	}
}