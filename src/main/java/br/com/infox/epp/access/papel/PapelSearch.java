package br.com.infox.epp.access.papel;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.entity.Papel_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PapelSearch extends PersistenceController {
    
    public List<Papel> getPapeisByNome(String descricao, Integer maxResult) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Papel> cq = cb.createQuery(Papel.class);
        Root<Papel> perfil = cq.from(Papel.class);
        cq.where(cb.like(cb.lower(perfil.get(Papel_.nome)), cb.lower(cb.literal("%" + descricao + "%"))));
        return getEntityManager().createQuery(cq).setMaxResults(maxResult).getResultList();
    }
    
}
