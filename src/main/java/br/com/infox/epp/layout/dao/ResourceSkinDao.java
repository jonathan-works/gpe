package br.com.infox.epp.layout.dao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.TypedQuery;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.layout.entity.ResourceSkin;
import br.com.infox.epp.layout.entity.Skin;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ResourceSkinDao extends Dao<ResourceSkin, Integer> {

	public ResourceSkinDao() {
		super(ResourceSkin.class);
	}
	
	public ResourceSkin findBySkinAndPath(Skin skin, String path) {
		String jpql = "select rs from ResourceSkin rs inner join rs.resource r where rs.skin = :skin and r.path = :path";
		TypedQuery<ResourceSkin> query = getEntityManager().createQuery(jpql, ResourceSkin.class);
		query.setParameter("skin", skin);
		query.setParameter("path", path);
		return getSingleResult(query);
	}
	
	
}
