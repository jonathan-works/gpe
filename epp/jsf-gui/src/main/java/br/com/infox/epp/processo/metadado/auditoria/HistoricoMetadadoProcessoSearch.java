package br.com.infox.epp.processo.metadado.auditoria;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;

public class HistoricoMetadadoProcessoSearch extends PersistenceController implements Serializable {

    private static final long serialVersionUID = 1L;

    public List<HistoricoMetadadoProcesso> findAll() {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<HistoricoMetadadoProcesso> cq = cb.createQuery(HistoricoMetadadoProcesso.class);
        
        From<?, HistoricoMetadadoProcesso> udcm = cq.from(HistoricoMetadadoProcesso.class);
      
        cq = cq.select(udcm);
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    public List<HistoricoMetadadoProcesso> findAllByProcessoMetadado(Integer idProcesso, Long idMetadado) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<HistoricoMetadadoProcesso> cq = cb.createQuery(HistoricoMetadadoProcesso.class);
        
        Root<HistoricoMetadadoProcesso> from = cq.from(HistoricoMetadadoProcesso.class);
        
        Predicate id = cb.equal(from.get(HistoricoMetadadoProcesso_.idProcesso), idProcesso);
        Predicate like = cb.equal(from.get(HistoricoMetadadoProcesso_.idMetadadoProcesso), idMetadado);
        
        Predicate restrictions = cb.and(like, id);
        
        cq = cq.select(from).where(restrictions);
        return getEntityManager().createQuery(cq).getResultList();
    }
}
