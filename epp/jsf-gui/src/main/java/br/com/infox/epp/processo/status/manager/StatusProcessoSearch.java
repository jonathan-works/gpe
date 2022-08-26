package br.com.infox.epp.processo.status.manager;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.Fluxo_;
import br.com.infox.epp.processo.status.entity.StatusProcesso;
import br.com.infox.epp.processo.status.entity.StatusProcesso_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class StatusProcessoSearch extends PersistenceController {

	public StatusProcesso getStatusByName(String name) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<StatusProcesso> cq = cb.createQuery(StatusProcesso.class);
		Root<StatusProcesso> statusProcesso = cq.from(StatusProcesso.class);
		cq.select(statusProcesso);
		cq.where(cb.equal(statusProcesso.get(StatusProcesso_.nome), cb.literal(name)));
		List<StatusProcesso> result = getEntityManager().createQuery(cq).setMaxResults(1).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}

	public List<StatusProcesso> getStatusProcessosAtivoRelacionados(Fluxo fluxo) {
		return getProcessosAtivo(fluxo, true);
	}
	
	public List<StatusProcesso> getStatusProcessosAtivoNaoRelacionados(Fluxo fluxo) {
		return getProcessosAtivo(fluxo, false);
	}
	
	private List<StatusProcesso> getProcessosAtivo(Fluxo fluxo, boolean selecionados) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<StatusProcesso> cq = cb.createQuery(StatusProcesso.class);
		Root<StatusProcesso> udm = cq.from(StatusProcesso.class);
		
		Subquery<Long> subquery = cq.subquery(Long.class);
		Root<StatusProcesso> subStatus = subquery.from(StatusProcesso.class);
		subquery.select(cb.literal(1L));
		
		Predicate pdcFlxo = cb.equal(subStatus.join(StatusProcesso_.fluxos).get(Fluxo_.idFluxo), fluxo.getIdFluxo());
		Predicate pdcIdStatus = cb.equal(udm, subStatus);
		
		subquery.where(pdcFlxo, pdcIdStatus);
		
		cq.select(udm);
		
		if (selecionados)
			cq.where(cb.exists(subquery));
		else
			cq.where(cb.exists(subquery).not());

		cq.where(cq.getRestriction(), cb.isTrue(udm.get(StatusProcesso_.ativo)));
		cq.orderBy(cb.asc(udm.get(StatusProcesso_.nome)));
		
		return getEntityManager().createQuery(cq).getResultList();
	}
    
	public Boolean existeStatusProcessoFluxoByNome(String nome, Integer idFluxo) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<StatusProcesso> statusProcesso = cq.from(StatusProcesso.class);
		Join<StatusProcesso, Fluxo> fluxo = statusProcesso.join(StatusProcesso_.fluxos, JoinType.INNER);
		cq.where(cb.equal(fluxo.get(Fluxo_.idFluxo), idFluxo), cb.equal(statusProcesso.get(StatusProcesso_.nome), cb.literal(nome)));
		cq.select(cb.count(statusProcesso));
		return getEntityManager().createQuery(cq).getSingleResult() > 0;
	}

	public StatusProcesso getStatusByNameAtivo(String name) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<StatusProcesso> cq = cb.createQuery(StatusProcesso.class);
		Root<StatusProcesso> statusProcesso = cq.from(StatusProcesso.class);
		cq.select(statusProcesso);
		cb.isTrue(statusProcesso.get(StatusProcesso_.ativo));
		cq.where(cb.equal(statusProcesso.get(StatusProcesso_.nome), cb.literal(name)));
		List<StatusProcesso> result = getEntityManager().createQuery(cq).setMaxResults(1).getResultList();
		return result.isEmpty() ? null : result.get(0);
	}
}
