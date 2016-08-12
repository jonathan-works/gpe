package br.com.infox.epp.processo.comunicacao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;

@Stateless
public class ModeloComunicacaoSearch {

    public List<ModeloComunicacao> getByProcessoAndTaskName(Integer idProcesso, String taskName) {
        CriteriaBuilder cb = getenEntityManager().getCriteriaBuilder();
        CriteriaQuery<ModeloComunicacao> cq = cb.createQuery(ModeloComunicacao.class);
        Root<DestinatarioModeloComunicacao> dmc = cq.from(DestinatarioModeloComunicacao.class);
        Join<DestinatarioModeloComunicacao, ModeloComunicacao> mc = dmc.join(DestinatarioModeloComunicacao_.modeloComunicacao, JoinType.INNER);
        Join<DestinatarioModeloComunicacao, Processo> processo = dmc.join(DestinatarioModeloComunicacao_.processo, JoinType.INNER);
        Join<ModeloComunicacao, Processo> processoPai = mc.join(ModeloComunicacao_.processo, JoinType.INNER);
        Join<Processo, MetadadoProcesso> tipoProcesso = processo.join(Processo_.metadadoProcessoList, JoinType.INNER);

        Subquery<Integer> exists = cq.subquery(Integer.class);
        exists.select(cb.literal(1));
        Root<Task> task = exists.from(Task.class);
        Root<ProcessInstance> processInstance = exists.from(ProcessInstance.class);
        exists.where(cb.equal(task.get("name"), taskName),
                cb.equal(task.get("key"), mc.get(ModeloComunicacao_.taskKey)),
                cb.equal(processInstance.<Long>get("id"), processoPai.get(Processo_.idJbpm)),
                cb.equal(task.<ProcessDefinition>get("processDefinition").<Long>get("id"), processInstance.<ProcessDefinition>get("processDefinition").<Long>get("id"))
        );

        cq.where(cb.isTrue(dmc.get(DestinatarioModeloComunicacao_.expedido)),
                cb.isNotNull(processo.get(Processo_.idJbpm)),
                cb.isNull(processo.get(Processo_.dataFim)),
                cb.equal(processoPai.get(Processo_.idProcesso), idProcesso),
                cb.equal(tipoProcesso.get(MetadadoProcesso_.metadadoType), EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType()),
                cb.equal(tipoProcesso.get(MetadadoProcesso_.valor), cb.literal(TipoProcesso.COMUNICACAO.value())),
                cb.exists(exists)
        );
        return getenEntityManager().createQuery(cq).getResultList();
    }

    private EntityManager getenEntityManager() {
        return EntityManagerProducer.getEntityManager();
    }
}
