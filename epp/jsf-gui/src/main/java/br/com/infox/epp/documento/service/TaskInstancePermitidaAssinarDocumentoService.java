package br.com.infox.epp.documento.service;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.cdi.dao.Dao;
import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.cdi.qualifier.GenericDao;
import br.com.infox.epp.documento.entity.TaskInstancePermitidaAssinarDocumento;
import br.com.infox.epp.documento.entity.TaskInstancePermitidaAssinarDocumento_;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.seam.exception.BusinessRollbackException;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TaskInstancePermitidaAssinarDocumentoService {

    @Inject
    @GenericDao
    private Dao<TaskInstancePermitidaAssinarDocumento, Long> dao;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void inserir(Long idTaskInstance, List<Integer> documentos) {
        if(documentos == null || documentos.isEmpty()) {
            return;
        }
        EntityManager em = EntityManagerProducer.getEntityManager();
        TaskInstance taskInstance = em.find(TaskInstance.class, idTaskInstance);
        if(taskInstance == null) {
            throw new BusinessRollbackException("TaskInstace não encontrado");
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Documento> query = cb.createQuery(Documento.class);
        Root<Documento> documento = query.from(Documento.class);

        Subquery<Integer> subquery = query.subquery(Integer.class);
        subquery.select(cb.literal(1));
        Root<TaskInstancePermitidaAssinarDocumento> taskInstancePermitidaAssinarDocumento = subquery.from(TaskInstancePermitidaAssinarDocumento.class);
        subquery.where(
            cb.equal(taskInstancePermitidaAssinarDocumento.get(TaskInstancePermitidaAssinarDocumento_.documento), documento),
            cb.equal(taskInstancePermitidaAssinarDocumento.get(TaskInstancePermitidaAssinarDocumento_.idTaskInstance), idTaskInstance)
        );

        query.where(
            documento.in(documentos),
            cb.not(cb.exists(subquery))
        );

        for (Documento doc : em.createQuery(query).getResultList()) {
            TaskInstancePermitidaAssinarDocumento taskInstancePermitidaAssinarDocumentoIns = new TaskInstancePermitidaAssinarDocumento();
            taskInstancePermitidaAssinarDocumentoIns.setIdTaskInstance(idTaskInstance);
            taskInstancePermitidaAssinarDocumentoIns.setDocumento(doc);
            dao.persist(taskInstancePermitidaAssinarDocumentoIns);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(Long idTaskInstance) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        em.createQuery("delete from TaskInstancePermitidaAssinarDocumento where idTaskInstance = :idTaskInstance")
            .setParameter("idTaskInstance", idTaskInstance)
            .executeUpdate();
        em.flush();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void remover(Long idTaskInstance, Integer documento) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        em.createQuery("delete from TaskInstancePermitidaAssinarDocumento where idTaskInstance = :idTaskInstance and documento = :documento")
            .setParameter("idTaskInstance", idTaskInstance)
            .setParameter("documento", documento)
            .executeUpdate();
        em.flush();
    }

}