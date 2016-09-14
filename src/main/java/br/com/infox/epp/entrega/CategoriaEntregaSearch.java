package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.entrega.entity.CategoriaEntrega;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem_;
import br.com.infox.epp.entrega.entity.CategoriaEntrega_;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.entrega.modelo.ModeloEntregaItem;
import br.com.infox.epp.entrega.modelo.ModeloEntregaItem_;
import br.com.infox.epp.entrega.modelo.ModeloEntrega_;

public class CategoriaEntregaSearch extends PersistenceController {

	public CategoriaEntrega getCategoriaEntregaByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<CategoriaEntrega> cq = cb.createQuery(CategoriaEntrega.class);
		Root<CategoriaEntrega> categoriaEntrega = cq.from(CategoriaEntrega.class);
		
		Predicate codigoIgual = cb.equal(cb.upper(categoriaEntrega.get(CategoriaEntrega_.codigo)), codigo.toUpperCase());
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
	
	private Subquery<Long> getSubqueryExisteItem(CriteriaQuery<?> cq, Path<ModeloEntrega> modeloEntrega, Long idCategoriaItemModelo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
		Subquery<Long> retorno = cq.subquery(Long.class);
		retorno.select(cb.literal(1L));
		Root<ModeloEntregaItem> mei = retorno.from(ModeloEntregaItem.class);
		retorno.where(
				cb.equal(mei.get(ModeloEntregaItem_.modeloEntrega), modeloEntrega),
				cb.equal(mei.get(ModeloEntregaItem_.item), idCategoriaItemModelo)
		);
		
		return retorno;
	}
	
	private Subquery<Long> getSubqueryIdsCategoriasModelos(CriteriaQuery<?> cq, Long idCategoriaItemModelo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
		Subquery<Long> retorno = cq.subquery(Long.class);
		Root<ModeloEntrega> modeloEntrega = retorno.from(ModeloEntrega.class);
		Join<ModeloEntrega, ModeloEntregaItem> modeloEntregaItem = modeloEntrega.join(ModeloEntrega_.itensModelo);
		Join<ModeloEntregaItem, CategoriaEntregaItem> categoriaEntregaItem = modeloEntregaItem.join(ModeloEntregaItem_.item);
		Join<CategoriaEntregaItem, CategoriaEntrega> categoriaEntrega = categoriaEntregaItem.join(CategoriaEntregaItem_.categoriaEntrega);

		Subquery<Long> subqueryExisteItem = getSubqueryExisteItem(cq, modeloEntrega, idCategoriaItemModelo);
		
		retorno.select(categoriaEntrega.get(CategoriaEntrega_.id));
		retorno.distinct(true);
		retorno.where(
				cb.isTrue(modeloEntrega.get(ModeloEntrega_.ativo)),
				cb.exists(subqueryExisteItem),
				cb.notEqual(categoriaEntregaItem.get(CategoriaEntregaItem_.id), idCategoriaItemModelo)
		);
		
		return retorno;
	}
	
	public List<CategoriaEntrega> getCategoriasModelos(Long idCategoriaItemModelo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<CategoriaEntrega> cq = cb.createQuery(CategoriaEntrega.class);
        Root<CategoriaEntrega> categoriaEntrega = cq.from(CategoriaEntrega.class);
        
        Subquery<Long> subqueryIdsCategoriasModelos = getSubqueryIdsCategoriasModelos(cq, idCategoriaItemModelo);
        
        cq.where(
        		cb.in(categoriaEntrega.get(CategoriaEntrega_.id)).value(subqueryIdsCategoriasModelos)
        );
		
        return getEntityManager().createQuery(cq).getResultList();
	}
	
}
