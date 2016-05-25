package br.com.infox.epp.entrega;

import java.io.Serializable;
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
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
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
        ListJoin<ModeloEntrega, CategoriaEntregaItem> itms2 = me2.join(ModeloEntrega_.itens,JoinType.INNER);
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
        From<?, CategoriaEntregaItem> itens = modeloEntrega.join(ModeloEntrega_.itens, JoinType.INNER);
        From<?, CategoriaItemRelacionamento> relacPais = itens.join(CategoriaEntregaItem_.itensPais, JoinType.INNER);
        
        List<Predicate> filtros = new ArrayList<>();
        
        filtros.add(createFiltroPai(relacPais, codigoItemPai));
        
        if (codigoLocalizacao != null) {
            Predicate filtroModeloEntregaComRestricao = createFiltroModeloEntregaComRestricao(cq, modeloEntrega, codigoLocalizacao);
            Predicate filtroModeloEntregaSemRestricao = createFiltroModeloEntregaSemRestricao(cq, modeloEntrega);
            Predicate filtroRestricoes = cb.or(filtroModeloEntregaComRestricao, filtroModeloEntregaSemRestricao);
            filtros.add(filtroRestricoes);
        }
        
        if (data != null) {
            filtros.add(createFiltroDataValida(modeloEntrega, data));
        }
        cq = cq.select(itens).where(filtros.toArray(new Predicate[filtros.size()]));
        return getEntityManager().createQuery(cq).getResultList();
    }

    private Predicate createFiltroPai(From<?, CategoriaItemRelacionamento> categegoriaItemRelacionamento, String codigoItemPai) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
        if (codigoItemPai != null && !codigoItemPai.trim().isEmpty()){
            From<?, CategoriaEntregaItem> pai = categegoriaItemRelacionamento.join(CategoriaItemRelacionamento_.itemPai, JoinType.INNER);
            return cb.equal(pai.get(CategoriaEntregaItem_.codigo), codigoItemPai);
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

    private Predicate createFiltroModeloEntregaSemRestricao(AbstractQuery<?> originalQuery, From<?,ModeloEntrega> modeloEntregaExterno) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
        Subquery<Long> sqSemRestricao = originalQuery.subquery(Long.class);
        Root<ModeloEntrega> modeloEntregaInterno = sqSemRestricao.from(ModeloEntrega.class);
        From<?, CategoriaEntregaItem> itensInterno = modeloEntregaInterno.join(ModeloEntrega_.itens, JoinType.INNER);
        itensInterno.join(CategoriaEntregaItem_.restricoes, JoinType.INNER);
        
        sqSemRestricao = sqSemRestricao.select(cb.literal(1L)).where(cb.equal(modeloEntregaExterno, modeloEntregaInterno));
        return cb.not(cb.exists(sqSemRestricao));
    }

    private Predicate createFiltroModeloEntregaComRestricao(AbstractQuery<?> originalQuery,
            From<?,ModeloEntrega> modeloEntregaExterno, String codigoLocalizacao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
        Subquery<Long> sqComRestricao = originalQuery.subquery(Long.class);
        Root<ModeloEntrega> modeloEntregaInterno = sqComRestricao.from(ModeloEntrega.class);
        From<?, CategoriaEntregaItem> itensInterno = modeloEntregaInterno.join(ModeloEntrega_.itens, JoinType.INNER);
        From<?, Localizacao> restricoes = itensInterno.join(CategoriaEntregaItem_.restricoes, JoinType.INNER);
        
        Predicate localizacaoIgualItm = cb.equal(restricoes.get(Localizacao_.codigo), codigoLocalizacao);
        
        sqComRestricao = sqComRestricao.select(cb.literal(1L)).where(localizacaoIgualItm, cb.equal(modeloEntregaExterno, modeloEntregaInterno));
        return cb.exists(sqComRestricao);
    }

}
