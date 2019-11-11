package br.com.infox.epp.entrega.checklist;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.dialect.Dialect;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.hibernate.util.HibernateUtil;

@Stateless
public class ChecklistService {

    @Inject
    private ChecklistSearch checklistSearch;

    /**
     * Método que retorna o {@link Checklist}.
     * Caso já exista, retorna o já existente. Caso não exista, cria um checklist.
     * @param pasta {@link Pasta} que o checklist deve se basear.
     * @return {@link Checklist}
     */
    public Checklist getByProcessoPasta(Processo processo, Pasta pasta) {
        Checklist checklist = checklistSearch.getByIdProcessoIdPasta(processo.getIdProcesso(), pasta.getId());
        if (checklist == null) {
            checklist = initChecklist(processo, pasta);
        } else {
            verifyNovosDocumentos(checklist);
        }
        return checklist;
    }

    private void verifyNovosDocumentos(Checklist checklist) {
        List<Documento> documentosNovos = checklistSearch.getNovosDocumentos(checklist);
        if (documentosNovos != null && !documentosNovos.isEmpty()) {
            initNovosDocumentos(checklist, documentosNovos);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void initNovosDocumentos(Checklist checklist, List<Documento> documentosNovos) {
        for (Documento doc : documentosNovos) {
            ChecklistDoc clDoc = new ChecklistDoc();
            clDoc.setChecklist(checklist);
            clDoc.setDocumento(doc);
            getEntityManager().persist(clDoc);
        }
        getEntityManager().flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Checklist initChecklist(Processo processo, Pasta pasta) {
        Checklist checkList = new Checklist();
        checkList.setDataCriacao(new Date());
        checkList.setUsuarioCriacao(Authenticator.getUsuarioLogado());
        checkList.setPasta(pasta);
        checkList.setProcesso(processo);
        getEntityManager().persist(checkList);
        getEntityManager().flush();
        initChecklistDoc(checkList, pasta);
        getEntityManager().flush();
        return checkList;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void initChecklistDoc(Checklist checkList, Pasta pasta) {
        Dialect dialect = HibernateUtil.getDialect();
        String nextVal = dialect.getSelectSequenceNextValString("sq_checklist_doc");
        String sql = "INSERT INTO tb_checklist_doc (id_checklist_doc, id_checklist, id_documento, nr_version) "
                + "SELECT " + nextVal + ", :idChecklist, d.id_documento, 0 "
                + "FROM tb_documento d "
                + "WHERE d.id_pasta = :idPastaEntrega";
        getEntityManager().createNativeQuery(sql)
                .setParameter("idChecklist", checkList.getId())
                .setParameter("idPastaEntrega", pasta.getId())
                .executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void update(ChecklistDoc clDoc) {
        getEntityManager().merge(clDoc);
        getEntityManager().flush();
    }

    private EntityManager getEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
