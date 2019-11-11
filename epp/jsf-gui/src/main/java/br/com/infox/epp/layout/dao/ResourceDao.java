package br.com.infox.epp.layout.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.layout.entity.Resource;
import br.com.infox.hibernate.util.HibernateUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ResourceDao extends Dao<Resource, Long> {

	public ResourceDao() {
		super(Resource.class);
	}
	
	public Resource findByCodigo(String codigo) {
		String jpql = "select r from Resource r where codigo = :codigo";
		TypedQuery<Resource> query = getEntityManager().createQuery(jpql, Resource.class);
		query.setParameter("codigo", codigo);
		HibernateUtil.enableCache(query);
		return getSingleResult(query);
	}

	public Resource findByPath(String path) {
		String jpql = "select r from Resource r where path = :path";
		TypedQuery<Resource> query = getEntityManager().createQuery(jpql, Resource.class);
		query.setParameter("path", path);
		HibernateUtil.enableCache(query);
		return getSingleResult(query);
	}
	
	/**
	 * Localiza um recurso que tenha o path iniciado com o parâmetro passado (retorna o primeiro resultado encontrado caso exista mais de um)
	 */
	public Resource findByStartingPath(String path) {
		String jpql = "select r from Resource r where path like :path";
		TypedQuery<Resource> query = getEntityManager().createQuery(jpql, Resource.class);
		query.setParameter("path", path + "%");
		HibernateUtil.enableCache(query);
		return getSingleResult(query);
	}
	
	protected <T> List<T> findAllByQuery(TypedQuery<T> query, Integer firstResult, Integer maxResults) {
		if(firstResult != null) {
			query.setFirstResult(firstResult);			
		}
		if(maxResults != null) {
			query.setMaxResults(maxResults);			
		}
		return query.getResultList();
	}
	
	public List<Resource> findAllByNome(String nome, Integer maxResults) {
		String jpql = "select r from Resource r where lower(nome) like lower(:nome)";
		TypedQuery<Resource> query = getEntityManager().createQuery(jpql, Resource.class);
		query.setParameter("nome", "%" + nome + "%");
		return findAllByQuery(query, null, maxResults);
	}
	
	public List<Resource> findAllByPath(String path, Integer maxResults) {
		String jpql = "select r from Resource r where lower(path) like lower(:path)";
		TypedQuery<Resource> query = getEntityManager().createQuery(jpql, Resource.class);
		query.setParameter("path", "%" + path + "%");
		return findAllByQuery(query, null, maxResults);
	}
}
