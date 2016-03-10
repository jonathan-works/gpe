package br.com.infox.core.log;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.log.LogErro;
import br.com.infox.epp.log.LogErro_;
import br.com.infox.epp.log.StatusLog;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class LogErroSearch {

    public List<LogErro> listAllPendentesEnvio() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<LogErro> cq = cb.createQuery(LogErro.class);
        Root<LogErro> logErro = cq.from(LogErro.class);
        cq.select(logErro);
        cq.where(
            cb.equal(logErro.get(LogErro_.status), StatusLog.PENDENTE)
        );
        return getEntityManager().createQuery(cq).getResultList();
        
    }
    
    public EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
