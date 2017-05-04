package br.com.infox.epp.redistribuicao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class RedistribuicaoSearch extends PersistenceController {

    public List<Redistribuicao> getRedistribuicoesByIdProcesso(Integer idProcesso) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Redistribuicao> cq = cb.createQuery(Redistribuicao.class);
        Root<Redistribuicao> redistribuicao = cq.from(Redistribuicao.class);
        Join<Redistribuicao, Processo> processo = redistribuicao.join(Redistribuicao_.processo);
        cq.where(cb.equal(processo.get(Processo_.idProcesso), idProcesso));
        cq.orderBy(cb.desc(redistribuicao.get(Redistribuicao_.data)));
        return getEntityManager().createQuery(cq).getResultList();
    }
}
