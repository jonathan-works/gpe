package br.com.infox.epp.processo.comunicacao.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.jbpm.taskmgmt.def.Task;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao_;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao;
import br.com.infox.epp.processo.comunicacao.ModeloComunicacao_;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.type.TipoProcesso;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class ComunicacaoSearch extends PersistenceController {

    @SuppressWarnings("unchecked")
    public Map<String, Object> getMaximoDiasCienciaMaisPrazo(Integer idProcesso, String taskName) {
        String jpql = "select coalesce(to_date(mpCiencia.valor), to_date(mpLimiteCiencia.valor)), "
                + "coalesce(max(d.prazo), 0) "
                
                + "from DestinatarioModeloComunicacao d "
                + "inner join d.processo p "
                + "inner join p.metadadoProcessoList mpTipoProcesso "
                + "inner join p.metadadoProcessoList mpLimiteCiencia "
                + "left join p.metadadoProcessoList mpCiencia with mpCiencia.metadadoType = :tipoMetadadoCiencia ";
        
        if (!StringUtil.isEmpty(taskName)) {
            jpql += "inner join d.modeloComunicacao mc ";
        }
                
        jpql += "where d.expedido = true and p.idJbpm is not null and p.dataFim is null "
            + "and p.processoPai.idProcesso = :idProcesso and mpTipoProcesso.valor in (:tiposComunicacao) "
            + "and mpLimiteCiencia.metadadoType = :tipoMetadadoLimiteCiencia and mpTipoProcesso.metadadoType = :tipoMetadadoTipoProcesso ";
        
        if (!StringUtil.isEmpty(taskName)) {
            jpql += " and exists(select 1 from Task t where t.name like :taskName and t.key = mc.taskKey) ";
        }
        
        jpql += " group by coalesce(to_date(mpCiencia.valor), to_date(mpLimiteCiencia.valor)) "
            + "order by DataUtilAdd('day', coalesce(max(d.prazo), 0), coalesce(to_date(mpCiencia.valor), to_date(mpLimiteCiencia.valor))) desc";
        
        Query query = getEntityManager().createQuery(jpql)
                .setParameter("tipoMetadadoCiencia", ComunicacaoMetadadoProvider.DATA_CIENCIA.getMetadadoType())
                .setParameter("idProcesso", idProcesso)
                .setParameter("tiposComunicacao", Arrays.asList(TipoProcesso.COMUNICACAO.value(), TipoProcesso.COMUNICACAO_NAO_ELETRONICA.value()))
                .setParameter("tipoMetadadoLimiteCiencia", ComunicacaoMetadadoProvider.LIMITE_DATA_CIENCIA.getMetadadoType())
                .setParameter("tipoMetadadoTipoProcesso", EppMetadadoProvider.TIPO_PROCESSO.getMetadadoType())
                .setParameter("taskName", taskName)
               .setMaxResults(1);
        
        if (!StringUtil.isEmpty(taskName)) {
            query.setParameter("taskName", taskName);
        }
        
        List<Object[]> resultList = query.getResultList();
        Object[] result = resultList.isEmpty() ? null : resultList.get(0);
        Map<String, Object> map = new HashMap<>(2);
        if (result != null) {
            map.put("dataCienciaOuLimite", result[0]);
            map.put("maiorPrazo", result[1]);
        }
        return map;
    }

    protected Predicate appendTaskNameFilter(Predicate predicate, CriteriaQuery<Tuple> cq, String taskName,
            Root<DestinatarioModeloComunicacao> destinatarioComunicacao) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        Join<DestinatarioModeloComunicacao, ModeloComunicacao> modeloComunicacao = destinatarioComunicacao.join(DestinatarioModeloComunicacao_.modeloComunicacao, JoinType.INNER);
        Subquery<Integer> existsSubquery = cq.subquery(Integer.class);
        existsSubquery.select(cb.literal(1));
        Root<Task> task = existsSubquery.from(Task.class);
        existsSubquery.where(
            cb.like(task.<String>get("name"), cb.literal(taskName)),
            cb.equal(task.get("key"), modeloComunicacao.get(ModeloComunicacao_.taskKey))
        );
        predicate = cb.and(cb.exists(existsSubquery), predicate);
        return predicate;
    }
    
}
