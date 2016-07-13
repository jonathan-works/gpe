package br.com.infox.ibpm.sinal;

import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;

@Stateless
public class SignalSearch extends PersistenceController{

	public boolean existeSignalByCodigo(String codigo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Signal> from = cq.from(Signal.class);
        cq.where(cb.equal(from.get(Signal_.codigo), cb.literal(codigo)));
        cq.select(cb.count(from));
        return getEntityManager().createQuery(cq).getSingleResult() > 0;
	}
}
