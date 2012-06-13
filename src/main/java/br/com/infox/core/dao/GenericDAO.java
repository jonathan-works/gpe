package br.com.infox.core.dao;

import static br.com.itx.util.EntityUtil.getSingleResult;

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

/**
 * DAO generico para consultas, persistencia
 * entre outros.
 * @author Daniel
 *
 */
@Name(GenericDAO.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class GenericDAO {

	public static final String NAME = "genericDAO";
	
	@In
	protected EntityManager entityManager;
	
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
	@SuppressWarnings("unchecked")
	public <T> List<T> findAll(Class<T> clazz) {
		StringBuilder sb = new StringBuilder();
		sb.append("select o from ").append(clazz.getName()).append(" o");
		return entityManager.createQuery(sb.toString()).getResultList();
	}
	
	@SuppressWarnings("unchecked")
	protected <T> List<T> getNamedResultList(String namedQuery,
			Map<String, Object> parameters) {
		Query q = getNamedQuery(namedQuery, parameters);
		return (List<T>) q.getResultList();
	}

	@SuppressWarnings("unchecked")
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
		entityManager.persist(object);
		entityManager.flush();
		return object;
	}
	
	public <T> T update(T object){
		entityManager.merge(object);
		entityManager.flush();
		return object;
	}
	
	public <T> T remove(T object){
		entityManager.remove(object);
		entityManager.flush();
		return object;
	}	
}