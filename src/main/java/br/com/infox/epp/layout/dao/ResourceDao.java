package br.com.infox.epp.layout.dao;

import java.util.List;

import javax.persistence.TypedQuery;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.epp.layout.entity.Resource;
import br.com.infox.epp.layout.entity.Resource.TipoResource;

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

}
