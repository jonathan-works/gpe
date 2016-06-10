package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.joda.time.DateTime;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Localizacao_;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem_;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento;
import br.com.infox.epp.entrega.entity.CategoriaItemRelacionamento_;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.entrega.modelo.ModeloEntregaItem;
import br.com.infox.epp.entrega.modelo.ModeloEntregaItem_;
import br.com.infox.epp.entrega.modelo.ModeloEntrega_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ModeloEntregaSearch extends PersistenceController {

	public ModeloEntrega findWithItems(List<CategoriaEntregaItem> items) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ModeloEntrega> cq = cb.createQuery(ModeloEntrega.class);
        Root<ModeloEntrega> from = cq.from(ModeloEntrega.class);
        
        Subquery<Long> sq1 = cq.subquery(Long.class);
        Root<ModeloEntrega> me1 = sq1.from(ModeloEntrega.class);
        sq1=sq1.select(cb.count(me1.join(ModeloEntrega_.itens,JoinType.INNER))).where(cb.equal(me1, from));
        
        Subquery<Long> sq2 = cq.subquery(Long.class);
        Root<ModeloEntrega> me2 = sq2.from(ModeloEntrega.class);
        Join<ModeloEntrega, ModeloEntregaItem> modeloEntregaItens2 = me2.join(ModeloEntrega_.itens, JoinType.INNER);
        Join<ModeloEntregaItem, CategoriaEntregaItem> itms2 = modeloEntregaItens2.join(ModeloEntregaItem_.item, JoinType.INNER);
        sq2=sq2.select(cb.count(itms2)).where(itms2.in(items), cb.equal(me2, from));
        
        List<Predicate> predicates=new ArrayList<>();
        if (items.size() > 0){
            predicates.add(cb.equal(sq1, items.size()));
            predicates.add(cb.equal(sq2, items.size()));
            //predicates.add(cb.equal(sq1, sq2));// Está implícito
        }
        cq=cq.select(from).where(predicates.toArray(new Predicate[predicates.size()]));
        List<ModeloEntrega> list = getEntityManager().createQuery(cq).setMaxResults(1).setFirstResult(0).getResultList();
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }
	
    public ModeloEntrega findById(Long id) {
       return getEntityManager().find(ModeloEntrega.class, id);
    }
    
	public List<ModeloEntrega> findAll(){
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<ModeloEntrega> cq = cb.createQuery(ModeloEntrega.class);
	    cq.from(ModeloEntrega.class);
	    return getEntityManager().createQuery(cq).getResultList();
	}

    
    public List<ModeloEntrega> getAgendasvencidas() {
        return getAgendasvencidas(DateTime.now().toDate());
    }

    public List<ModeloEntrega> getAgendasvencidas(Date data) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ModeloEntrega> cq = cb.createQuery(ModeloEntrega.class);

        Root<ModeloEntrega> modeloEntrega = cq.from(ModeloEntrega.class);
        Predicate prazoExpirado = cb.lessThan(modeloEntrega.get(ModeloEntrega_.dataLimite), data);
        Predicate sinalNaoDisparado = cb.isFalse(modeloEntrega.get(ModeloEntrega_.sinalDisparado));

        Predicate restricoes = cb.and(prazoExpirado, sinalNaoDisparado,
        		cb.isTrue(modeloEntrega.get(ModeloEntrega_.ativo)));

        Order ordem = cb.asc(modeloEntrega.get(ModeloEntrega_.dataLimite));

        cq = cq.select(modeloEntrega).where(restricoes).orderBy(ordem);
        return entityManager.createQuery(cq).getResultList();
    }
    
    public List<CategoriaEntregaItem> getCategoriasEItensComEntrega(String codigoItemPai, String codigoLocalizacao, Date data){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
        CriteriaQuery<CategoriaEntregaItem> cq = cb.createQuery(CategoriaEntregaItem.class);
        Root<ModeloEntrega> modeloEntrega = cq.from(ModeloEntrega.class);
        From<?, ModeloEntregaItem> modeloEntregaItem = modeloEntrega.join(ModeloEntrega_.itens, JoinType.INNER);
        From<?, CategoriaEntregaItem> itens = modeloEntregaItem.join(ModeloEntregaItem_.item, JoinType.INNER);
        From<?, CategoriaItemRelacionamento> relacPais = itens.join(CategoriaEntregaItem_.itensPais, JoinType.INNER);
        
        List<Predicate> filtros = new ArrayList<>();
        
        filtros.add(createFiltroPai(relacPais, modeloEntregaItem, codigoItemPai));
        filtros.add(cb.isTrue(modeloEntrega.get(ModeloEntrega_.ativo)));
        
        if (codigoLocalizacao != null) {
            Predicate filtroModeloEntregaComRestricao = createFiltroModeloEntregaComRestricao(cq, itens, codigoLocalizacao);
            Predicate filtroModeloEntregaSemRestricao = createFiltroModeloEntregaSemRestricao(cq, itens);
            Predicate filtroRestricoes = cb.or(filtroModeloEntregaComRestricao, filtroModeloEntregaSemRestricao);
            filtros.add(filtroRestricoes);
        }
        
        if (data != null) {
            filtros.add(createFiltroDataValida(modeloEntrega, data));
        }
        cq = cq.select(itens).where(filtros.toArray(new Predicate[filtros.size()]));
        return getEntityManager().createQuery(cq).getResultList();
    }

    private Predicate createFiltroPai(From<?, CategoriaItemRelacionamento> categegoriaItemRelacionamento, From<?, ModeloEntregaItem> modeloEntregaItem, String codigoItemPai) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
        if (codigoItemPai != null && !codigoItemPai.trim().isEmpty()){
            From<?, CategoriaEntregaItem> pai = categegoriaItemRelacionamento.join(CategoriaItemRelacionamento_.itemPai, JoinType.INNER);
            return cb.and(
            	cb.equal(pai.get(CategoriaEntregaItem_.codigo), codigoItemPai),
            	cb.equal(pai, modeloEntregaItem.get(ModeloEntregaItem_.itemPai))
            );
        } else {
            return cb.isNull(categegoriaItemRelacionamento.get(CategoriaItemRelacionamento_.itemPai));
        }
    }

    private Predicate createFiltroDataValida(From<?,ModeloEntrega> modeloEntrega, Date data) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Path<Date> dataLimite = modeloEntrega.get(ModeloEntrega_.dataLimite);
        Path<Date> dataLiberacao = modeloEntrega.get(ModeloEntrega_.dataLiberacao);
        
        Predicate dataLimiteNotNullEDataNoIntervalo= cb.and(cb.isNotNull(dataLimite), cb.between(cb.literal(data), dataLiberacao, dataLimite));
        Predicate dataLimiteNullEDataPosterior=cb.and(cb.isNull(dataLimite), cb.lessThanOrEqualTo(dataLiberacao, data));
        Predicate dataValida = cb.or(dataLimiteNotNullEDataNoIntervalo, dataLimiteNullEDataPosterior);
        return dataValida;
    }

    private Predicate createFiltroModeloEntregaSemRestricao(AbstractQuery<?> originalQuery, Path<CategoriaEntregaItem> itemOriginal) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	Subquery<Integer> subquery = originalQuery.subquery(Integer.class);
    	Root<CategoriaEntregaItem> item = subquery.from(CategoriaEntregaItem.class);
    	item.join(CategoriaEntregaItem_.restricoes, JoinType.INNER);
    	subquery.select(cb.literal(1));
    	subquery.where(cb.equal(item, itemOriginal));
    	return cb.exists(subquery).not();
    }

    private Predicate createFiltroModeloEntregaComRestricao(AbstractQuery<?> originalQuery, Path<CategoriaEntregaItem> itemOriginal, String codigoLocalizacao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Subquery<Integer> subquery = originalQuery.subquery(Integer.class);
        Root<CategoriaEntregaItem> item = subquery.from(CategoriaEntregaItem.class);
        Join<CategoriaEntregaItem, Localizacao> restricoes = item.join(CategoriaEntregaItem_.restricoes, JoinType.INNER);
        subquery.where(cb.equal(item, itemOriginal), cb.equal(restricoes.get(Localizacao_.codigo), codigoLocalizacao));
        subquery.select(cb.literal(1));
        return cb.exists(subquery);
    }
    
    public boolean itemPertenceAoModelo(CategoriaEntregaItem item, ModeloEntrega modeloEntrega) {
    	EntityManager entityManager = getEntityManager();
    	CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    	CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
    	Root<ModeloEntregaItem> modeloEntregaItem = query.from(ModeloEntregaItem.class);
    	query.where(
    		cb.equal(modeloEntregaItem.get(ModeloEntregaItem_.modeloEntrega), modeloEntrega),
    		cb.equal(modeloEntregaItem.get(ModeloEntregaItem_.item), item)
    	);
    	query.select(cb.literal(1));
    	return !entityManager.createQuery(query).setMaxResults(1).getResultList().isEmpty();
    }
}
