package br.com.infox.epp.processo.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.entity.VariavelInicioProcesso;
import br.com.infox.epp.processo.entity.VariavelInicioProcesso_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class VariavelInicioProcessoSearch extends PersistenceController {
    
    public VariavelInicioProcesso getVariavelInicioProcesso(Processo processo, String name) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<VariavelInicioProcesso> cq = cb.createQuery(VariavelInicioProcesso.class);
        Root<VariavelInicioProcesso> variavelInicioProcesso = cq.from(VariavelInicioProcesso.class);
        cq.select(variavelInicioProcesso);
        cq.where(
            cb.equal(variavelInicioProcesso.get(VariavelInicioProcesso_.processo).get(Processo_.idProcesso), cb.literal(processo.getIdProcesso())),
            cb.equal(variavelInicioProcesso.get(VariavelInicioProcesso_.name), cb.literal(name))
        );
        List<VariavelInicioProcesso> result = getEntityManager().createQuery(cq).setMaxResults(1).getResultList();
        return result.isEmpty() ? null : result.get(0);
    }
    
    public List<VariavelInicioProcesso> findAll(Processo processo) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<VariavelInicioProcesso> cq = cb.createQuery(VariavelInicioProcesso.class);
        Root<VariavelInicioProcesso> variavelInicioProcesso = cq.from(VariavelInicioProcesso.class);
        cq.select(variavelInicioProcesso);
        cq.where(
            cb.equal(variavelInicioProcesso.get(VariavelInicioProcesso_.processo).get(Processo_.idProcesso), cb.literal(processo.getIdProcesso()))
        );
        return getEntityManager().createQuery(cq).getResultList();
    }
}
