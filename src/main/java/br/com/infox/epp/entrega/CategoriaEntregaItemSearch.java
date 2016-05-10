package br.com.infox.epp.entrega;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem_;
import br.com.infox.epp.entrega.entity.CategoriaEntrega_;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento_;

@Stateless
public class CategoriaEntregaItemSearch {

	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}
	
	public List<CategoriaEntregaItem> getCategoriaEntregaItemByCodigoPaiAndCodigoCategoria(String codigoItemPai, String codigoCategoria) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntregaItem> cq = cb.createQuery(CategoriaEntregaItem.class);
		Root<CategoriaEntregaItem> categoriaEntregaItem = cq.from(CategoriaEntregaItem.class);
		Join<CategoriaEntregaItem, CategoriaItemRelacionamento> relacionamentosPais = categoriaEntregaItem.join(CategoriaEntregaItem_.itensPais);
		Path<CategoriaEntregaItem> itensPais = relacionamentosPais.join(CategoriaItemRelacionamento_.itemPai);
		Path<CategoriaEntrega> categoria = categoriaEntregaItem.join(CategoriaEntregaItem_.categoriaEntrega);
		
		Predicate codigoPaiIgual = cb.equal(itensPais.get(CategoriaEntregaItem_.codigo), codigoItemPai);
		Predicate codigoCategoriaIgual = cb.equal(categoria.get(CategoriaEntrega_.codigo), codigoCategoria);
		
		cq = cq.select(categoriaEntregaItem).where(codigoPaiIgual, codigoCategoriaIgual);
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	public CategoriaEntregaItem getCategoriaEntregaItemByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntregaItem> cq = cb.createQuery(CategoriaEntregaItem.class);
		Root<CategoriaEntregaItem> categoriaEntregaItem = cq.from(CategoriaEntregaItem.class);
		Predicate codigoIgual = cb.equal(categoriaEntregaItem.get(CategoriaEntregaItem_.codigo), codigo);
		cq = cq.select(categoriaEntregaItem).where(codigoIgual);
		return getEntityManager().createQuery(cq).getSingleResult();
	}
	
	public List<CategoriaEntregaItem> getCategoriaEntregaItemByDescricaoLike(String descricao) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntregaItem> cq = cb.createQuery(CategoriaEntregaItem.class);
		Root<CategoriaEntregaItem> categoriaEntregaItem = cq.from(CategoriaEntregaItem.class);
		Predicate descricaoLike = cb.like(cb.lower(categoriaEntregaItem.get(CategoriaEntregaItem_.descricao)), "%" + descricao + "%");
		cq = cq.select(categoriaEntregaItem).where(descricaoLike);
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	public List<CategoriaEntregaItem> getCategoriaEntregaItemByCodigoCategoriaAndDescricaoLike(String codigoCategoria, String descricao) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntregaItem> cq = cb.createQuery(CategoriaEntregaItem.class);
		Root<CategoriaEntregaItem> categoriaEntregaItem = cq.from(CategoriaEntregaItem.class);
		Path<CategoriaEntrega> categoria = categoriaEntregaItem.join(CategoriaEntregaItem_.categoriaEntrega);
		
		Predicate codigoCategoriaIgual = cb.equal(categoria.get(CategoriaEntrega_.codigo), codigoCategoria);
		Predicate descricaoLike = cb.like(cb.lower(categoriaEntregaItem.get(CategoriaEntregaItem_.descricao)), "%" + descricao + "%");
		
		cq = cq.select(categoriaEntregaItem).where(codigoCategoriaIgual, descricaoLike);
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	public List<CategoriaEntregaItem> list() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntregaItem> cq = cb.createQuery(CategoriaEntregaItem.class);
		cq.from(CategoriaEntregaItem.class);
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	
}
