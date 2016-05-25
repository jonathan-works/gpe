package br.com.infox.epp.entrega.modelo;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;

@Stateless
public class ModeloEntregaSearch {
	
    public List<ModeloEntrega> getAgendasvencidas() {
        return getAgendasvencidas(new Date());
    }

    public List<ModeloEntrega> getAgendasvencidas(Date data) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ModeloEntrega> cq = cb.createQuery(ModeloEntrega.class);

        Root<ModeloEntrega> modeloEntrega = cq.from(ModeloEntrega.class);
        Predicate prazoExpirado = cb.lessThan(modeloEntrega.get(ModeloEntrega_.dataLimite), data);
        Predicate sinalNaoDisparado = cb.isFalse(modeloEntrega.get(ModeloEntrega_.sinalDisparado));

        Predicate restricoes = cb.and(prazoExpirado, sinalNaoDisparado);

        Order ordem = cb.asc(modeloEntrega.get(ModeloEntrega_.dataLimite));

        cq = cq.select(modeloEntrega).where(restricoes).orderBy(ordem);
        List<ModeloEntrega> resultList = entityManager.createQuery(cq).getResultList();
        return resultList;
    }

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
