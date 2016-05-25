package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem_;
import br.com.infox.epp.entrega.entity.CategoriaEntrega_;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento_;

@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class CategoriaEntregaItemSearch extends PersistenceController {

	protected List<CategoriaEntregaItem> findWithFilters(String codigoItemPai, String codigoCategoria) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntregaItem> cq = cb.createQuery(CategoriaEntregaItem.class);
		Root<CategoriaEntregaItem> categoriaEntregaItem = cq.from(CategoriaEntregaItem.class);
		
		List<Predicate> where = new ArrayList<>();
		
		if(codigoItemPai != null) {
			Join<CategoriaEntregaItem, CategoriaItemRelacionamento> relacionamentosPais = categoriaEntregaItem.join(CategoriaEntregaItem_.itensPais);
			Path<CategoriaEntregaItem> itensPais = relacionamentosPais.join(CategoriaItemRelacionamento_.itemPai);			
			Predicate codigoPaiIgual = cb.equal(itensPais.get(CategoriaEntregaItem_.codigo), codigoItemPai);
			where.add(codigoPaiIgual);
		}
		
		if(codigoCategoria != null) {
			Path<CategoriaEntrega> categoria = categoriaEntregaItem.join(CategoriaEntregaItem_.categoriaEntrega);			
			Predicate codigoCategoriaIgual = cb.equal(categoria.get(CategoriaEntrega_.codigo), codigoCategoria);
			where.add(codigoCategoriaIgual);
		}
		
		cq = cq.select(categoriaEntregaItem).where(where.toArray(new Predicate[0]));
		return getEntityManager().createQuery(cq).getResultList();
		
	}
	
	public List<CategoriaEntregaItem> getCategoriaEntregaItemByCodigoCategoria(String codigoCategoria) {
		return findWithFilters(null, codigoCategoria);		
	}
	
	public List<CategoriaEntregaItem> getCategoriaEntregaItemByCodigoPai(String codigoItemPai) {
		return findWithFilters(codigoItemPai, null);		
	}
	
	public List<CategoriaEntregaItem> getCategoriaEntregaItemByCodigoPaiAndCodigoCategoria(String codigoItemPai, String codigoCategoria) {
		return findWithFilters(codigoItemPai, codigoCategoria);
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

    public List<CategoriaEntregaItem> getCategoriaEntregaItemByCodigoPaiAndDescricao(String codigoItemPai, String descricao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CategoriaEntregaItem> cq = cb.createQuery(CategoriaEntregaItem.class);
        From<?,CategoriaEntrega> categoriaEntrega;
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
        SetJoin<?, CategoriaEntregaItem> items = categoriaEntrega.join(CategoriaEntrega_.itemsFilhos);
        Predicate descricaoSemelhante = cb.like(cb.lower(items.get(CategoriaEntregaItem_.descricao)), "%"+descricao.toLowerCase()+"%");
        predicates.add(descricaoSemelhante);
        
        cq = cq.select(items).where(predicates.toArray(new Predicate[predicates.size()]));
        return getEntityManager().createQuery(cq).getResultList();
    }
    
	public List<CategoriaEntregaItem> getByCategoriaAndLocalizacao(CategoriaEntrega categoria,Localizacao localizacao) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntregaItem> cq = cb.createQuery(CategoriaEntregaItem.class);
		Root<CategoriaEntregaItem> categoriaEntregaItem = cq.from(CategoriaEntregaItem.class);

		Predicate categoriaIgual = cb.equal(categoriaEntregaItem.get(CategoriaEntregaItem_.categoriaEntrega),
				categoria);
		Predicate itemPossuiLocalizacao = cb.isMember(localizacao,
				categoriaEntregaItem.get(CategoriaEntregaItem_.restricoes));
		cq.select(categoriaEntregaItem).where(categoriaIgual, itemPossuiLocalizacao);

		return getEntityManager().createQuery(cq).getResultList();
	}
    
	
	
}
