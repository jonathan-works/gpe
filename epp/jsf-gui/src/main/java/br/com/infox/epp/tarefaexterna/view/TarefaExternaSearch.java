package br.com.infox.epp.tarefaexterna.view;

import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jbpm.context.exe.variableinstance.StringInstance;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.processo.entity.ProcessoJbpm;
import br.com.infox.epp.processo.entity.ProcessoJbpm_;

@Stateless
public class TarefaExternaSearch {

    public ProcessoJbpm getProcessoJbpmByUUID(UUID uuid) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ProcessoJbpm> query = cb.createQuery(ProcessoJbpm.class);
        Root<ProcessoJbpm> processoJbpm = query.from(ProcessoJbpm.class);

        Subquery<Integer> sqStringInstance = query.subquery(Integer.class);
        sqStringInstance.select(cb.literal(1));
        Root<StringInstance> stringInstance = sqStringInstance.from(StringInstance.class);
        sqStringInstance.where(
            cb.equal(stringInstance.get("processInstance"), processoJbpm.get(ProcessoJbpm_.processInstance)),
            cb.equal(stringInstance.get("name"), CadastroTarefaExternaView.PARAM_UUID_TAREFA_EXTERNA),
            cb.equal(stringInstance.<String>get("value"), uuid.toString())
        );
        query.where(cb.exists(sqStringInstance));

        try {
            return em.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


}