package br.com.infox.epp.entrega;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.entrega.entity.CategoriaEntregaItem;
import br.com.infox.epp.entrega.modelo.ModeloEntrega;
import br.com.infox.epp.entrega.modelo.ModeloEntrega_;

public class ModeloEntregaSearch {

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
            predicates.add(cb.equal(sq1, sq2));
        }
        cq=cq.select(from).where(predicates.toArray(new Predicate[predicates.size()]));
        List<ModeloEntrega> list = getEntityManager().createQuery(cq).setMaxResults(1).setFirstResult(0).getResultList();
        return (list == null || list.isEmpty()) ? null : list.get(0);
    }
    
    private EntityManager getEntityManager(){
        return EntityManagerProducer.getEntityManager();
    }

}
