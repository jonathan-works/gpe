package br.com.infox.epp.layout.dao;

import javax.persistence.TypedQuery;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.layout.entity.Resource;
import br.com.infox.hibernate.util.HibernateUtil;

public class ResourceDao extends Dao<Resource, Long> {

	public ResourceDao() {
		super(Resource.class);
	}
	
	public Resource findByCodigo(String codigo) {
		String jpql = "from Resource r where codigo = :codigo";
		TypedQuery<Resource> query = getEntityManager().createQuery(jpql, Resource.class);
		query.setParameter("codigo", codigo);
		HibernateUtil.enableCache(query);
		return getSingleResult(query);
	}

}
