package br.com.infox.epp.fluxo.monitor;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.google.common.base.Strings;

import br.com.infox.cdi.producer.EntityManagerProducer;

public class MonitorProcessoDataModel extends LazyDataModel<MonitorProcessoInstanceDTO> {
    private static final long serialVersionUID = 1L;
    
    private long processDefinitionId;
    private String nodeKey;
    
    public MonitorProcessoDataModel(long processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    @Override
    public List<MonitorProcessoInstanceDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        EntityManager entityManager = EntityManagerProducer.getEntityManager();
        
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        CriteriaQuery<MonitorProcessoInstanceDTO> query = cb.createQuery(MonitorProcessoInstanceDTO.class);
       
        configureQuery(query, filters);
        configureQuery(countQuery, filters);
        countQuery.select(cb.count(countQuery.getRoots().iterator().next()));
        
        setRowCount(entityManager.createQuery(countQuery).getSingleResult().intValue());
        
        return entityManager.createQuery(query).setFirstResult(first).setMaxResults(pageSize).getResultList();
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void configureQuery(CriteriaQuery query, Map<String, Object> filters) {
        EntityManager entityManager = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        Root<MonitorInstanciasProcesso> root = query.from(MonitorInstanciasProcesso.class);
        query.where(cb.equal(root.get(MonitorInstanciasProcesso_.idProcessDefinition), processDefinitionId));
        
        String numeroProcesso = (String)filters.get("numero"); 
        if(!Strings.isNullOrEmpty(numeroProcesso)) {
            query.where(query.getRestriction(), cb.like(root.get(MonitorInstanciasProcesso_.numeroProcesso), cb.literal("%" + numeroProcesso + "%")));
        	
        }
        
        if (!Strings.isNullOrEmpty(nodeKey)) {
            query.where(query.getRestriction(), cb.equal(root.get(MonitorInstanciasProcesso_.nodeKey), nodeKey));
        }

        query.select(cb.construct(MonitorProcessoInstanceDTO.class, root.get(MonitorInstanciasProcesso_.numeroProcesso), 
                root.get(MonitorInstanciasProcesso_.nodeName), root.get(MonitorInstanciasProcesso_.dataInicio),
                root.get(MonitorInstanciasProcesso_.state), root.get(MonitorInstanciasProcesso_.idToken)));
    }
    
    public String getNodeKey() {
        return nodeKey;
    }
    
    public void setNodeKey(String nodeKey) {
        this.nodeKey = nodeKey;
    }
}
