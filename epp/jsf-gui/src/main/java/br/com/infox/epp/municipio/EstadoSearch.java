package br.com.infox.epp.municipio;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;

@Stateless
public class EstadoSearch {

	private EntityManager getEntityManager(){
		return EntityManagerProducer.getEntityManager();
	}

	public List<Estado> findAll() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Estado> query = cb.createQuery(Estado.class);
		Root<Estado> estado = query.from(Estado.class);
		query.orderBy(cb.asc(estado.get(Estado_.nome)));
		return getEntityManager().createQuery(query).getResultList();
	}

	public Estado retrieveEstadoByCodigo(String codigo) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Estado> query = cb.createQuery(Estado.class);
        Root<Estado> estado = query.from(Estado.class);
        query.where(cb.equal(estado.get(Estado_.codigo), codigo));
        try {
            return getEntityManager().createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
	}

}
