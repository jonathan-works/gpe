package br.com.infox.epp.access.perfiltemplate;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.PerfilTemplate;
import br.com.infox.epp.access.entity.PerfilTemplate_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PerfilTemplateSearch extends PersistenceController {

    public List<PerfilTemplate> getPerfisTemplatesByDescricao(String descricao, Integer maxResult) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PerfilTemplate> cq = cb.createQuery(PerfilTemplate.class);
        Root<PerfilTemplate> perfil = cq.from(PerfilTemplate.class);
        cq.where(cb.like(cb.lower(perfil.get(PerfilTemplate_.descricao)), cb.lower(cb.literal("%" + descricao + "%"))));
        return getEntityManager().createQuery(cq).setMaxResults(maxResult).getResultList();
    }
}
