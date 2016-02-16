package br.com.infox.ibpm.sinal;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.dao.Dao;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class SignalDao extends Dao<Signal, Long> {

    public SignalDao() {
        super(Signal.class);
    }
    
    public List<Signal> findAllAtivo() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Signal> cq = cb.createQuery(Signal.class);
        Root<Signal> from = cq.from(Signal.class);
        cq.select(from);
        cq.where(cb.isTrue(from.get(Signal_.ativo)));
        return getEntityManager().createQuery(cq).getResultList();
    }

}
