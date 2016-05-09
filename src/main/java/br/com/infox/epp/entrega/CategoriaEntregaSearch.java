package br.com.infox.epp.entrega;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntrega_;

@Stateless
public class CategoriaEntregaSearch {

	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	public CategoriaEntrega getCategoriaEntregaByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntrega> cq = cb.createQuery(CategoriaEntrega.class);
		Root<CategoriaEntrega> categoriaEntrega = cq.from(CategoriaEntrega.class);
		Predicate codigoIgual = cb.equal(categoriaEntrega.get(CategoriaEntrega_.codigo), codigo);
		cq = cq.select(categoriaEntrega).where(codigoIgual);
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	public List<CategoriaEntrega> getCategoriaEntregaRoot() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntrega> cq = cb.createQuery(CategoriaEntrega.class);
		Root<CategoriaEntrega> categoriaEntrega = cq.from(CategoriaEntrega.class);
		Predicate categoriaRoot = cb.isNull(categoriaEntrega.get(CategoriaEntrega_.categoriaEntregaPai));
		cq = cq.select(categoriaEntrega).where(categoriaRoot);
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	public List<CategoriaEntrega> list() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntrega> cq = cb.createQuery(CategoriaEntrega.class);
		Root<CategoriaEntrega> categoriaEntrega = cq.from(CategoriaEntrega.class);
		return getEntityManager().createQuery(cq).getResultList();
	}
	
}
