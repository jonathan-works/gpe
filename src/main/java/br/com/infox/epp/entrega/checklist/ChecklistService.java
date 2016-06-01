package br.com.infox.epp.entrega.checklist;

import java.util.Date;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.dialect.Dialect;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.entrega.documentos.Entrega;
import br.com.infox.hibernate.util.HibernateUtil;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ChecklistService {

    @Inject
    private ChecklistSearch checklistSearch;

    /**
     * Método que retorna o {@link Checklist}.
     * Caso já exista, retorna o já existente. Caso não exista, cria um checklist.
     * @param entrega {@link Entrega} que o checklist deve se basear.
     * @return {@link Checklist}
     */
    public Checklist getByEntrega(Entrega entrega) {
        Checklist checklist = checklistSearch.getByIdEntrega(entrega.getId());
        if (checklist == null) {
            checklist = initChecklist(entrega);
        } else {
            // TODO adicionar verificação de documentos novos
        }
        return checklist;
    }

    // TODO falta testar
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public Checklist initChecklist(Entrega entrega) {
        Checklist checkList = new Checklist();
        checkList.setDataCriacao(new Date());
        checkList.setUsuarioCriacao(Authenticator.getUsuarioLogado());
        checkList.setEntrega(entrega);
        getEntityManager().persist(checkList);
        getEntityManager().flush();
        initChecklistDoc(checkList, entrega);
        getEntityManager().flush();
        return checkList;
    }

    // TODO testar essa mágica
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    private void initChecklistDoc(Checklist checkList, Entrega entrega) {
        Dialect dialect = HibernateUtil.getDialect();
        String nextVal = dialect.getSequenceNextValString("sq_checklist_doc");
        String sql = "INSERT INTO tb_checklist_doc (id_checklist_doc, id_checklist, id_documento, nr_version) "
                + nextVal + ", :idChecklist, d.id_documento, 0 "
                + "FROM tb_documento d "
                + "WHERE d.id_pasta = :idPastaEntrega";
        getEntityManager().createNativeQuery(sql)
                .setParameter("idChecklist", checkList.getId())
                .setParameter("idPastaEntrega", entrega.getPasta().getId())
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
