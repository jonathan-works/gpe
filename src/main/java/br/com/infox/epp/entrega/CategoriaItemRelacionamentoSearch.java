package br.com.infox.epp.entrega;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem_;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento_;

public class CategoriaItemRelacionamentoSearch {
	
	public EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public CategoriaItemRelacionamento getByCodigoPaiAndFilho(String codigoPai, String codigoFilho) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaItemRelacionamento> cq = cb.createQuery(CategoriaItemRelacionamento.class);
		Root<CategoriaItemRelacionamento> categoriaItemRelacionamento = cq.from(CategoriaItemRelacionamento.class);
		Path<CategoriaEntregaItem> itemFilho = categoriaItemRelacionamento.join(CategoriaItemRelacionamento_.itemFilho);
		Predicate filtroPai;
		if (codigoPai!=null){
		    Path<CategoriaEntregaItem> itemPai = categoriaItemRelacionamento.join(CategoriaItemRelacionamento_.itemPai);		    
		    filtroPai = cb.equal(itemPai.get(CategoriaEntregaItem_.codigo), codigoPai);
		} else {
		    filtroPai = cb.isNull(categoriaItemRelacionamento.get(CategoriaItemRelacionamento_.itemPai));
		}
		Predicate codigoFilhoIgual = cb.equal(itemFilho.get(CategoriaEntregaItem_.codigo), codigoFilho);
		
		cq = cq.select(categoriaItemRelacionamento).where(filtroPai, codigoFilhoIgual);
		return getEntityManager().createQuery(cq).getSingleResult();
	}


}
