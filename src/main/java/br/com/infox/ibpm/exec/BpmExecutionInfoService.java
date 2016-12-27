package br.com.infox.ibpm.exec;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.SingularAttribute;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso;
import br.com.infox.epp.fluxo.entity.DefinicaoProcesso_;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;

@Stateless
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class BpmExecutionInfoService implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private EntityManager entityManager = BeanManager.INSTANCE.getReference(EntityManager.class);

	public Map<String,Long> getTokensCount(String processDefinitionName){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Token> tok = cq.from(Token.class);
		Join<Token, Node> nod = tok.join(singularAttribute(Token.class, "node", Node.class));
		Join<Token, ProcessInstance> pi = tok.join(singularAttribute(Token.class, "processInstance", ProcessInstance.class));
		Join<ProcessInstance, ProcessDefinition> pd = pi.join(singularAttribute(ProcessInstance.class, "processDefinition", ProcessDefinition.class));
		
		Predicate r1 = cb.equal(pd.get(singularAttribute(ProcessDefinition.class, "name", String.class)), processDefinitionName);
		
		Path<String> key = nod.get(singularAttribute(Node.class, "key", String.class));
		Selection<Long> count = cb.countDistinct(tok.get(singularAttribute(Token.class, "id", Long.class)));
		cq = cq.multiselect(key, count);
		cq = cq.where(cb.and(r1));
		cq = cq.groupBy(key);
		return tupleListToMap(key, count, entityManager.createQuery(cq).getResultList());
	}

	private Map<String, Long> tupleListToMap(TupleElement<String> key, TupleElement<Long> value, List<Tuple> resultList) {
		Map<String,Long> result = new HashMap<>();
		for (Tuple tuple : resultList) {
			result.put(tuple.get(key), tuple.get(value));
		}
		return result;
	}
	
	private <X,Y> SingularAttribute<? super X, Y> singularAttribute(Class<X> path, String attribute, Class<Y> type){
		return entityManager.getMetamodel().entity(path).getSingularAttribute(attribute, type);
	}

	public List<Fluxo> getFluxosValidos() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Fluxo> cq = cb.createQuery(Fluxo.class);
		
		Root<Fluxo> fluxo = cq.from(Fluxo.class);
		Join<Fluxo, DefinicaoProcesso> definicaoProcesso = fluxo.join(Fluxo_.definicaoProcesso, JoinType.INNER);
		
		Predicate ativo = cb.isTrue(fluxo.get(Fluxo_.ativo));
		Predicate publicado = cb.isTrue(fluxo.get(Fluxo_.publicado));
		Predicate possuiSvg = cb.and(
	        cb.isNotNull(definicaoProcesso.get(DefinicaoProcesso_.svg)), 
	        cb.not(cb.equal(cb.trim(definicaoProcesso.get(DefinicaoProcesso_.svg)), cb.literal("")))
        );
		
		cq = cq.select(fluxo);
		cq = cq.where(ativo, publicado, possuiSvg);
		cq = cq.orderBy(cb.asc(fluxo.get(Fluxo_.fluxo)));
		return entityManager.createQuery(cq).getResultList();
	}
	
}
