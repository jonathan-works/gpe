package br.com.infox.epp.entrega.checklist;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento;
import br.com.infox.epp.documento.entity.ClassificacaoDocumento_;
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.epp.entrega.documentos.Entrega_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Documento_;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class ChecklistSearch {

    // TODO check if need this method
    public Checklist getByIdEntrega(Long idEntrega) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Checklist> cq = cb.createQuery(Checklist.class);
        Root<Checklist> cl = cq.from(Checklist.class);
        Join<Checklist, Entrega> pc = cl.join(Checklist_.entrega, JoinType.INNER);
        cq.select(cl);
        cq.where(cb.equal(pc.get(Entrega_.id), idEntrega));
        List<Checklist> resultList = getEntityManager().createQuery(cq).getResultList();
        return (resultList != null && !resultList.isEmpty()) ? resultList.get(0) : null;
    }

    // TODO verificar método
    public Boolean hasItemNaoConforme(Checklist cl) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        cq.select(cb.literal(1));
        Root<ChecklistDoc> clDoc = cq.from(ChecklistDoc.class);
        cq.where(cb.equal(clDoc.get(ChecklistDoc_.checklist), cl),
                cb.equal(clDoc.get(ChecklistDoc_.situacao), ChecklistSituacao.NCO));
        try {
            Integer result = getEntityManager().createQuery(cq).getSingleResult();
            return result == 1;
        } catch (NoResultException nre) {
            return false;
        }
    }

    // TODO vertificar pois não está sendo utilizado
    public List<ChecklistDoc> getChecklistDoc(Integer idChecklist) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ChecklistDoc> cq = cb.createQuery(ChecklistDoc.class);
        Root<ChecklistDoc> clDoc = cq.from(ChecklistDoc.class);
        Join<ChecklistDoc, Checklist> cl = clDoc.join(ChecklistDoc_.checklist, JoinType.INNER);
        cq.select(clDoc);
        cq.where(cb.equal(cl.get(Checklist_.id), idChecklist));
        return getEntityManager().createQuery(cq).getResultList();
    }

    // TODO verificar método
    public List<ChecklistDoc> getChecklistDocByChecklistSituacao(Checklist cl, ChecklistSituacao situacao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ChecklistDoc> cq = cb.createQuery(ChecklistDoc.class);
        Root<ChecklistDoc> clDoc = cq.from(ChecklistDoc.class);
        Join<ChecklistDoc, Documento> documentoPC = clDoc.join(ChecklistDoc_.documento, JoinType.INNER);
        Join<Documento, ClassificacaoDocumento> classificacao = documentoPC.join(Documento_.classificacaoDocumento, JoinType.INNER);
        cq.select(clDoc);
        cq.where(cb.equal(clDoc.get(ChecklistDoc_.checklist), cl),
                cb.equal(clDoc.get(ChecklistDoc_.situacao), situacao));
        cq.orderBy(cb.asc(classificacao.get(ClassificacaoDocumento_.descricao)), cb.asc(documentoPC.get(Documento_.descricao)));
        return getEntityManager().createQuery(cq).getResultList();
    }

    // TODO verificar método
    public boolean hasItemSemSituacao(Checklist checklist) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Integer> cq = cb.createQuery(Integer.class);
        Root<ChecklistDoc> clDoc = cq.from(ChecklistDoc.class);
        cq.select(cb.literal(1));
        cq.where(cb.equal(clDoc.get(ChecklistDoc_.checklist), checklist),
                cb.isNull(clDoc.get(ChecklistDoc_.situacao)));
        try {
            Integer result = getEntityManager().createQuery(cq).getSingleResult();
            return result == 1;
        } catch (NoResultException e) {
            return false;
        }
    }

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
