package br.com.infox.epp.layout.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.layout.entity.Resource;
import br.com.infox.epp.layout.entity.ResourceBin;
import br.com.infox.epp.layout.entity.Skin;
import br.com.infox.hibernate.util.HibernateUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ResourceBinDao extends Dao<ResourceBin, Integer> {

	public ResourceBinDao() {
		super(ResourceBin.class);
	}
	
	public List<ResourceBin> findByPath(String path) {
		String jpql = "select rb from ResourceBin rb inner join rb.resource r where r.path = :path";
		TypedQuery<ResourceBin> query = getEntityManager().createQuery(jpql, ResourceBin.class);
		query.setParameter("path", path);
		return query.getResultList();
	}
	
	public List<ResourceBin> findByResource(Resource resource) {
		return findByPath(resource.getPath());
	}

	public ResourceBin findBySkinAndCodigo(Skin skin, String codigoResource) {
		String jpql = "select r from ResourceBin r inner join r.skins s inner join r.resource res where s = :skin and res.codigo = :codigo";
		TypedQuery<ResourceBin> query = getEntityManager().createQuery(jpql, ResourceBin.class);
		query.setParameter("skin", skin);
		query.setParameter("codigo", codigoResource);
		return getSingleResult(query);
	}
	
	public ResourceBin findBySkinAndPath(Skin skin, String path) {
		String jpql = "select r from ResourceBin r inner join r.skins s inner join r.resource res where s = :skin and res.path = :path";
		TypedQuery<ResourceBin> query = getEntityManager().createQuery(jpql, ResourceBin.class);
		query.setParameter("skin", skin);
		query.setParameter("path", path);
		HibernateUtil.enableCache(query);
		return getSingleResult(query);
	}
}
