package br.com.infox.epp.processo.comunicacao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.jbpm.context.exe.VariableInstance;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.service.VariaveisJbpmAnaliseDocumento;
import br.com.infox.epp.processo.type.TipoProcesso;

@Stateless
public class ModeloComunicacaoSearch extends PersistenceController {

    public List<ModeloComunicacao> getByProcessoAndTaskName(Integer idProcesso, String taskName) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ModeloComunicacao> cq = cb.createQuery(ModeloComunicacao.class);
        From<?, ModeloComunicacao> modeloComunicacao = cq.from(ModeloComunicacao.class);
        From<?, Processo> paiComunicacao = modeloComunicacao.join(ModeloComunicacao_.processo, JoinType.INNER);
        From<?, DestinatarioModeloComunicacao> destinatario = modeloComunicacao.join(ModeloComunicacao_.destinatarios, JoinType.INNER);
        From<?, Processo> comunicacao = destinatario.join(DestinatarioModeloComunicacao_.processo, JoinType.INNER);
        
        Predicate restrictions = cb.and(
            cb.equal(paiComunicacao.get(Processo_.idProcesso), idProcesso),
            cb.isTrue(destinatario.get(DestinatarioModeloComunicacao_.expedido)),
            createPredicateProcessoExisteEAtivo(comunicacao),
            createPredicateTaskNameEqual(cq, modeloComunicacao, paiComunicacao, taskName)
        );
        
        cq.select(modeloComunicacao).where(restrictions);
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<Processo> getRespostasComunicacaoByProcessoAndTaskName(Integer idProcesso, String taskName, boolean somenteProrrogacaoPrazo){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        
        CriteriaQuery<Processo> cq = cb.createQuery(Processo.class);
        From<?, ModeloComunicacao> modeloComunicacao = cq.from(ModeloComunicacao.class);
        From<?, Processo> paiComunicacao = modeloComunicacao.join(ModeloComunicacao_.processo, JoinType.INNER);
        From<?, DestinatarioModeloComunicacao> destinatario = modeloComunicacao.join(ModeloComunicacao_.destinatarios, JoinType.INNER);
        From<?, Processo> comunicacao = destinatario.join(DestinatarioModeloComunicacao_.processo, JoinType.INNER);
        
        Predicate restrictions = cb.and(
            cb.equal(paiComunicacao.get(Processo_.idProcesso), idProcesso),
            cb.isTrue(destinatario.get(DestinatarioModeloComunicacao_.expedido)),
            createPredicateProcessoExisteEAtivo(comunicacao),
            createPredicateTaskNameEqual(cq, modeloComunicacao, paiComunicacao, taskName)
        );

        From<?, Processo> respostaComunicacao = comunicacao.join(Processo_.processosFilhos, JoinType.INNER);
        restrictions = cb.and(restrictions, createPredicateRespostaComunicacao(cq, respostaComunicacao, somenteProrrogacaoPrazo));
        
        cq.select(respostaComunicacao).where(restrictions);
        return getEntityManager().createQuery(cq).getResultList();
    }
    
    private Predicate createPredicateProcessoExisteEAtivo(From<?, Processo> comunicacao){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        return cb.and(
            cb.isNotNull(comunicacao.get(Processo_.idJbpm)), 
            cb.isNull(comunicacao.get(Processo_.dataFim))
        );
    }
    
    private Predicate createPredicateRespostaComunicacao(AbstractQuery<?> cq, From<?,Processo> respostaComunicacao, boolean somenteProrrogacaoPrazo){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Predicate predicate = createPredicateIsAnaliseDocumento(respostaComunicacao);
        if (somenteProrrogacaoPrazo){
            predicate = cb.and(predicate, createPredicateIsPedidoProrrogacaoPrazo(cq, respostaComunicacao));
        }
        return predicate;
    }
    
    private Predicate createPredicateIsAnaliseDocumento(From<?,Processo> analiseDocumento){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        From<?,MetadadoProcesso> metadadoProcesso = analiseDocumento.join(Processo_.metadadoProcessoList, JoinType.INNER);
        return cb.and(
            cb.equal(metadadoProcesso.get(MetadadoProcesso_.metadadoType), EppMetadadoProvider.TIPO_PROCESSO),
            cb.equal(metadadoProcesso.get(MetadadoProcesso_.valor), TipoProcesso.DOCUMENTO.toString())
        );
    }
    
    private Predicate createPredicateIsPedidoProrrogacaoPrazo(AbstractQuery<?> cq, From<?,Processo> analiseDocumento){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<VariableInstance> variableInstance = cq.from(VariableInstance.class);
        From<?, ProcessInstance> analiseDocumentoJbpm = variableInstance.<VariableInstance,ProcessInstance>join("processInstance", JoinType.INNER);
        return cb.and(
            cb.equal(analiseDocumento.get(Processo_.idJbpm), analiseDocumentoJbpm.get("id")),
            cb.equal(variableInstance.get("name"), VariaveisJbpmAnaliseDocumento.PEDIDO_PRORROGACAO_PRAZO),
            cb.equal(variableInstance.get("value"), "T")
        );
    }
    
    private Predicate createPredicateTaskNameEqual(AbstractQuery<?> cq,
            From<?,ModeloComunicacao> modeloComunicacao, From<?, Processo> paiComunicacao, String taskName){
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Root<TaskInstance> taskInstance = cq.from(TaskInstance.class);
        Join<?, ProcessInstance> processInstance = taskInstance.<TaskInstance,ProcessInstance>join("processInstance", JoinType.INNER);
        Join<?,Task> task = taskInstance.<TaskInstance,Task>join("task", JoinType.INNER);
        return cb.and(
            cb.equal(task.get("name"), taskName),
            cb.equal(task.get("key"), modeloComunicacao.get(ModeloComunicacao_.taskKey)),
            cb.equal(processInstance.get("id"), paiComunicacao.get(Processo_.idJbpm))
        );
    }
    
}
