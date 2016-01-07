package br.com.infox.epp.layout.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.layout.entity.Resource;
import br.com.infox.epp.layout.entity.Resource.TipoResource;
import br.com.infox.epp.layout.entity.Skin;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ResourceDao extends Dao<Resource, Integer> {

	public ResourceDao() {
		super(Resource.class);
	}
	
	public List<Resource> findByPath(String path) {
		String jpql = "from Resource r where r.path = :path";
		TypedQuery<Resource> query = getEntityManager().createQuery(jpql, Resource.class);
		query.setParameter("path", path);
		return query.getResultList();
	}
	
	public List<Resource> findByTipo(TipoResource tipo) {
		return findByPath(tipo.getPath());
	}

	public Resource findBySkinAndPath(Skin skin, String path) {
		String jpql = "select r from Resource r inner join r.skins s where s = :skin and r.path = :path";
		TypedQuery<Resource> query = getEntityManager().createQuery(jpql, Resource.class);
		query.setParameter("skin", skin);
		query.setParameter("path", path);
		return getSingleResult(query);
	}
}
