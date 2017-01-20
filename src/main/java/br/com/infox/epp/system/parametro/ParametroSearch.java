package br.com.infox.epp.system.parametro;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.system.entity.Parametro;
import br.com.infox.epp.system.entity.Parametro_;

import javax.ejb.TransactionAttribute;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ParametroSearch extends PersistenceController {
    
    public String getValorParametro(String nomeParametro) {
        EntityManager entityManager = getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> query = cb.createQuery(String.class);
        Root<Parametro> parametro = query.from(Parametro.class);
        query.select(parametro.get(Parametro_.valorVariavel));
        query.where(cb.equal(parametro.get(Parametro_.nomeVariavel), nomeParametro));
        
        try {
            return entityManager.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return "";
        }
    }
}
