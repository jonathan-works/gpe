package br.com.infox.epp.entrega;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem_;

@Stateless
public class CategoriaEntregaItemSearch {

	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	public CategoriaEntregaItem getCategoriaEntregaItemByCodigo(String codigoLocalizacao) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntregaItem> cq = cb.createQuery(CategoriaEntregaItem.class);
		Root<CategoriaEntregaItem> categoriaEntregaItem = cq.from(CategoriaEntregaItem.class);
		Predicate codigoIgual = cb.equal(categoriaEntregaItem.get(CategoriaEntregaItem_.codigo), codigoLocalizacao);
		cq = cq.select(categoriaEntregaItem).where(codigoIgual);
		return getEntityManager().createQuery(cq).getSingleResult();
	}
	
}
