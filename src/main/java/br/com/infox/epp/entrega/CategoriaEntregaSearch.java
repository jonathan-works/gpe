package br.com.infox.epp.entrega;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem_;
import br.com.infox.epp.entrega.entity.CategoriaEntrega_;

@Stateless
public class CategoriaEntregaSearch implements Serializable {

    private static final long serialVersionUID = 1L;

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
		cq.from(CategoriaEntrega.class);
		return getEntityManager().createQuery(cq).getResultList();
	}
	
	public List<CategoriaEntrega> getCategoriasFilhasComDescricao(String codigoItemPai, String descricao) {
	        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	        CriteriaQuery<CategoriaEntrega> cq = cb.createQuery(CategoriaEntrega.class);
	        Path<CategoriaEntrega> categoriaEntrega;
	        List<Predicate> predicates = new ArrayList<>();
	        
	        if (codigoItemPai == null){
	            categoriaEntrega = cq.from(CategoriaEntrega.class);
	            Predicate categoriaRoot = cb.isNull(categoriaEntrega.get(CategoriaEntrega_.categoriaEntregaPai));
	            predicates.add(categoriaRoot);
	        } else {
	            Root<CategoriaEntregaItem> categoriaEntregaItemPai = cq.from(CategoriaEntregaItem.class);
	            Join<?, CategoriaEntrega> categoriaEntregaPai = categoriaEntregaItemPai.join(CategoriaEntregaItem_.categoriaEntrega);
	            categoriaEntrega = categoriaEntregaPai.join(CategoriaEntrega_.categoriasFilhas, JoinType.INNER);
	            
	            predicates.add(cb.equal(categoriaEntregaItemPai.get(CategoriaEntregaItem_.codigo), codigoItemPai));
	        }
	        Predicate descricaoSemelhante = cb.like(cb.lower(categoriaEntrega.get(CategoriaEntrega_.descricao)), "%"+descricao.toLowerCase()+"%");
	        predicates.add(descricaoSemelhante);
	        
	        cq = cq.select(categoriaEntrega).where(predicates.toArray(new Predicate[predicates.size()]));
	        return getEntityManager().createQuery(cq).getResultList();
	    }
	
}
