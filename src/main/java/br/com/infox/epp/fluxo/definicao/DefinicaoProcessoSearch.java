package br.com.infox.epp.fluxo.definicao;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso;
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DefinicaoProcessoSearch extends PersistenceController {

    public DefinicaoProcesso getByCodigoFluxo(String codigoFluxo) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DefinicaoProcesso> query = cb.createQuery(DefinicaoProcesso.class);
        Root<DefinicaoProcesso> root = query.from(DefinicaoProcesso.class);
        Join<DefinicaoProcesso, Fluxo> fluxo = root.join(DefinicaoProcesso_.fluxo, JoinType.INNER);
        query.where(cb.equal(fluxo.get(Fluxo_.codFluxo), codigoFluxo));
        return entityManager.createQuery(query).getSingleResult();
    }
}
