package br.com.infox.epp.processo.comunicacao.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao_;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao_;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.util.time.Date;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ComunicacaoSearch extends PersistenceController {

    public Map<String, Object> getMaximoDiasCienciaMaisPrazo(Integer idProcesso, String taskName) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Tuple> cq = cb.createTupleQuery();
        Root<DestinatarioModeloComunicacao> destinatarioComunicacao = cq.from(DestinatarioModeloComunicacao.class);
        Join<DestinatarioModeloComunicacao, Processo> processo = destinatarioComunicacao.join(DestinatarioModeloComunicacao_.processo, JoinType.INNER);
        Join<Processo, MetadadoProcesso> tipoProcesso = processo.join(Processo_.metadadoProcessoList, JoinType.INNER);
        Join<Processo, MetadadoProcesso> limiteCiencia = processo.join(Processo_.metadadoProcessoList, JoinType.INNER);
        
        Expression<Date> dataCiencia = cb.function("to_date", Date.class, limiteCiencia.get(MetadadoProcesso_.valor));
        Expression<Integer> maiorPrazo = cb.max(destinatarioComunicacao.get(DestinatarioModeloComunicacao_.prazo));
        Expression<Date> maiorPrazoComCiencia = cb.function("DataUtilAdd", Date.class, cb.literal("day"), maiorPrazo, dataCiencia);  
        
        cq.select(cb.tuple(dataCiencia.alias("dataLimiteCiencia"), maiorPrazo.alias("maiorPrazo")));
        
        Predicate predicate = cb.and(
            cb.isTrue(destinatarioComunicacao.get(DestinatarioModeloComunicacao_.expedido)),
            cb.isNotNull(processo.get(Processo_.idJbpm)),
            cb.isNull(processo.get(Processo_.dataFim)),
            cb.equal(processo.get(Processo_.processoPai).get(Processo_.idProcesso), cb.literal(idProcesso)),
            cb.equal(tipoProcesso.get(MetadadoProcesso_.metadadoType), cb.literal(EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType())),
            cb.equal(tipoProcesso.get(MetadadoProcesso_.valor), cb.literal(TipoProcesso.COMUNICACAO.value())),
            cb.equal(limiteCiencia.get(MetadadoProcesso_.metadadoType), cb.literal(ComunicacaoMetadadoProvider.LIMITE_DATA_CIENCIA.getMetadadoType()))
        );
        
        if (!StringUtil.isEmpty(taskName)) {
            predicate = appendTaskNameFilter(predicate, cq, processo, taskName, destinatarioComunicacao);
        }
        cq.groupBy(limiteCiencia.get(MetadadoProcesso_.valor));
        cq.where(predicate);
        cq.orderBy(cb.desc(maiorPrazoComCiencia));
        List<Tuple> resultList = getEntityManager().createQuery(cq).setMaxResults(1).getResultList();
        Tuple tuple = resultList.isEmpty() ? null : resultList.get(0);
        Map<String, Object> map = new HashMap<>(2);
        if (tuple != null) {
            map.put("dataLimiteCiencia", tuple.get("dataLimiteCiencia", Date.class));
            map.put("maiorPrazo", tuple.get("maiorPrazo", Integer.class));
        }
        return map;
    }

    protected Predicate appendTaskNameFilter(Predicate predicate, CriteriaQuery<Tuple> cq, Join<?, Processo> processo, String taskName,
            Root<DestinatarioModeloComunicacao> destinatarioComunicacao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Join<DestinatarioModeloComunicacao, ModeloComunicacao> modeloComunicacao = destinatarioComunicacao.join(DestinatarioModeloComunicacao_.modeloComunicacao, JoinType.INNER);
        Join<Processo, Processo> processoPai = processo.join(Processo_.processoPai, JoinType.INNER);
        Subquery<Integer> existsSubquery = cq.subquery(Integer.class);
        existsSubquery.select(cb.literal(1));
        Root<Task> task = existsSubquery.from(Task.class);
        Root<ProcessInstance> processInstance = existsSubquery.from(ProcessInstance.class);
        existsSubquery.where(
            cb.like(task.<String>get("name"), cb.literal(taskName)),
            cb.equal(task.get("key"), modeloComunicacao.get(ModeloComunicacao_.taskKey)),
            cb.equal(processInstance.<Long>get("id"), processoPai.get(Processo_.idJbpm)),
            cb.equal(task.<ProcessDefinition>get("processDefinition").<Long>get("id"), processInstance.<ProcessDefinition>get("processDefinition").<Long>get("id"))
        );
        predicate = cb.and(cb.exists(existsSubquery), predicate);
        return predicate;
    }
    
}
