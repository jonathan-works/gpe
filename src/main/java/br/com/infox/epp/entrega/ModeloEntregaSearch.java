package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.joda.time.DateTime;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
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

    public ModeloEntrega findById(Integer id) {
        return getEntityManager().find(ModeloEntrega.class, id);
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

}
