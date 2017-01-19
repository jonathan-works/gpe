package br.com.infox.epp.processo.status.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class StatusProcessoSearch extends PersistenceController {
    
    public StatusProcesso getStatusByName(String name) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<StatusProcesso> cq = cb.createQuery(StatusProcesso.class);
        Root<StatusProcesso> statusProcesso = cq.from(StatusProcesso.class);
        cq.select(statusProcesso);
        cq.where(
            cb.equal(statusProcesso.get(StatusProcesso_.nome), cb.literal(name))
        );
        List<StatusProcesso> result = getEntityManager().createQuery(cq).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
    
	public Boolean existeStatusProcessoByNome(String nome) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatusProcesso> statusProcesso = cq.from(StatusProcesso.class);
		cq.where(cb.equal(statusProcesso.get(StatusProcesso_.nome), cb.literal(nome)));
		cq.select(cb.count(statusProcesso));
		return getEntityManager().createQuery(cq).getSingleResult() > 0;
	}

}
