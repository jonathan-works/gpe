package br.com.infox.core.dao;

import static br.com.itx.util.EntityUtil.getSingleResult;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.constants.WarningConstants;
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
	
	public <T> T persist(T object) throws DAOException{
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
	
	public <T> T update(T object) throws DAOException{
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
	
	public <T> T remove(final T object) throws DAOException{
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
	
	private <T> T processExceptions(DAOActionInterface<T> action,T object,String msg) throws DAOException {
	    StopWatch sw = new StopWatch();
        sw.start();
	    try {
	        return action.execute(object);
	    } catch (Throwable t) {
	    	throw new DAOException(t);
	    } finally {
	    	Util.rollbackTransactionIfNeeded();
	    }
	}
}