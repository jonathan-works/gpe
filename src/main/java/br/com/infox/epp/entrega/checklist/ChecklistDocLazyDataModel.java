package br.com.infox.epp.entrega.checklist;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Documento_;

public class ChecklistDocLazyDataModel extends LazyDataModel<ChecklistDoc> {
    private static final long serialVersionUID = 1L;

    private final String FILTER_CLASSIFICACAO_DOCUMENTO = "documento.classificacaoDocumento";
    private final String FILTER_SITUACAO = "situacao";
    private final String FILTER_NOME_DOCUMENTO = "documento.descricao";
    private final String JOIN_DOCUMENTO__ALIAS = "documento";
    private final String JOIN_CLASSIFICACAO_DOCUMENTO_ALIAS = "classificacaoDocumento";

    private Checklist cl;
    private Map<Long, ChecklistDoc> cache;

    public ChecklistDocLazyDataModel() {}

    public ChecklistDocLazyDataModel(Checklist cl) {
        this.cl = cl;
        this.cache = new HashMap<>();
    }

    @Override
    public ChecklistDoc getRowData(String rowKey) {
        return cache.get(Long.parseLong(rowKey));
    }

    @Override
    public Object getRowKey(ChecklistDoc object) {
        return object.getId();
    }

    @Override
    public List<ChecklistDoc> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ChecklistDoc> cq = cb.createQuery(ChecklistDoc.class);
        createQueryGetChecklistDocByCheckList(cq, cb, true);
        
        CriteriaQuery<Long> cqCount = cb.createQuery(Long.class);
        createQueryGetChecklistDocByCheckList(cqCount, cb, false);
        cqCount.select(cb.count(cqCount.getRoots().iterator().next()));

        if (filters.containsKey(FILTER_CLASSIFICACAO_DOCUMENTO)) {
            ClassificacaoDocumento classificacao = getEntityManager().find(ClassificacaoDocumento.class, Integer.valueOf((String) filters.get(FILTER_CLASSIFICACAO_DOCUMENTO)));
            appendClassificacaoFilter(cq, cb, classificacao);
            appendClassificacaoFilter(cqCount, cb, classificacao);
        }
        if (filters.containsKey(FILTER_SITUACAO)) {
            ChecklistSituacao situacao = ChecklistSituacao.valueOf((String) filters.get(FILTER_SITUACAO));
            appendSituacaoFilter(cq, cb, situacao);
            appendSituacaoFilter(cqCount, cb, situacao);
        }
        if (filters.containsKey(FILTER_NOME_DOCUMENTO)) {
            String nome = (String) filters.get(FILTER_NOME_DOCUMENTO);
            appendNomeDocumentoFilter(cq, cb, nome);
            appendNomeDocumentoFilter(cqCount, cb, nome);
        }

        TypedQuery<ChecklistDoc> query = getEntityManager().createQuery(cq);
        query = query.setFirstResult(first).setMaxResults(pageSize);

        List<ChecklistDoc> checklistDocumentos = query.getResultList();
        for (ChecklistDoc checklistDocumento : checklistDocumentos) {
            cache.put(checklistDocumento.getId(), checklistDocumento);
        }

        TypedQuery<Long> queryCount = getEntityManager().createQuery(cqCount);
        setRowCount(queryCount.getSingleResult().intValue());

        return checklistDocumentos;
    }

    @SuppressWarnings("unchecked")
    private void appendClassificacaoFilter(CriteriaQuery<?> cq, CriteriaBuilder cb, ClassificacaoDocumento classificacao) {
        Root<ChecklistDoc> clDoc = (Root<ChecklistDoc>) cq.getRoots().iterator().next();
        Join<ChecklistDoc, Documento> documento = (Join<ChecklistDoc, Documento>) retrieveJoin(clDoc.getJoins(), JOIN_DOCUMENTO__ALIAS);
        cq.where(cq.getRestriction(), cb.equal(documento.get(Documento_.classificacaoDocumento), classificacao));
    }

    @SuppressWarnings("unchecked")
    private void appendSituacaoFilter(CriteriaQuery<?> cq, CriteriaBuilder cb, ChecklistSituacao situacao) {
        if (ChecklistSituacao.NIF.equals(situacao)) {
            Root<ChecklistDoc> clDoc = (Root<ChecklistDoc>) cq.getRoots().iterator().next();
            cq.where(cq.getRestriction(), cb.isNull(clDoc.get(ChecklistDoc_.situacao)));
        } else {
            Root<ChecklistDoc> clDoc = (Root<ChecklistDoc>) cq.getRoots().iterator().next();
            cq.where(cq.getRestriction(), cb.equal(clDoc.get(ChecklistDoc_.situacao), situacao));
        }
    }

    @SuppressWarnings("unchecked")
    private void appendNomeDocumentoFilter(CriteriaQuery<?> cq, CriteriaBuilder cb, String nome) {
        Root<ChecklistDoc> clDoc = (Root<ChecklistDoc>) cq.getRoots().iterator().next();
        Join<ChecklistDoc, Documento> documento = (Join<ChecklistDoc, Documento>) retrieveJoin(clDoc.getJoins(), JOIN_DOCUMENTO__ALIAS);
        String pattern = "%" + nome.toLowerCase() + "%";
        cq.where(cq.getRestriction(), cb.like(cb.lower(documento.get(Documento_.descricao)), pattern));
    }

    private void createQueryGetChecklistDocByCheckList(CriteriaQuery<?> cq, CriteriaBuilder cb, boolean order) {
        Root<ChecklistDoc> clDoc = cq.from(ChecklistDoc.class);
        Join<ChecklistDoc, Checklist> cl = clDoc.join(ChecklistDoc_.checklist, JoinType.INNER);
        Join<ChecklistDoc, Documento> dpc = clDoc.join(ChecklistDoc_.documento, JoinType.INNER);
        Join<Documento, ClassificacaoDocumento> cd = dpc.join(Documento_.classificacaoDocumento, JoinType.INNER);
        dpc.alias(JOIN_DOCUMENTO__ALIAS);
        cd.alias(JOIN_CLASSIFICACAO_DOCUMENTO_ALIAS);
        if (order) {
            cq.orderBy(cb.asc(cd.get(ClassificacaoDocumento_.descricao)), cb.asc(dpc.get(Documento_.descricao)));
        }
        cq.where(cb.equal(cl.get(Checklist_.id), this.cl.getId()));
    }

    private Join<?, ?> retrieveJoin(Set<Join<ChecklistDoc, ?>> joins, String alias) {
        if (alias == null) return null;
        for (Join<?, ?> join : joins) {
            if (alias.equals(join.getAlias())) {
                return join;
            }
        }
        return null;
    }

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
