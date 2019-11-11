package br.com.infox.epp.documento.pasta;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaRestricao;
import br.com.infox.epp.processo.documento.entity.PastaRestricao_;
import br.com.infox.epp.processo.documento.entity.Pasta_;
import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.entity.Processo_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PastaSearch extends PersistenceController {

	public Pasta getPastaByCodigoIdProcesso(String codigoPasta, Integer idProcesso) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Pasta> cq = cb.createQuery(Pasta.class);
		Root<Pasta> from = cq.from(Pasta.class);
		Join<Pasta, Processo> processo = from.join(Pasta_.processo);
		cq.where(cb.equal(from.get(Pasta_.codigo), codigoPasta),
				cb.equal(processo.get(Processo_.idProcesso), idProcesso));
		try {
			return getEntityManager().createQuery(cq).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<Pasta> getPastasPublicas(Processo processo) {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
	    CriteriaQuery<Pasta> query = cb.createQuery(Pasta.class);
	    Root<Pasta> pasta = query.from(Pasta.class);
	    
	    Subquery<Integer> subquery = query.subquery(Integer.class);
	    Root<PastaRestricao> restricao = subquery.from(PastaRestricao.class);
	    subquery.where(cb.equal(restricao.get(PastaRestricao_.pasta), pasta), 
	            cb.equal(restricao.get(PastaRestricao_.tipoPastaRestricao), PastaRestricaoEnum.D),
	            cb.isTrue(restricao.get(PastaRestricao_.read)));
	    subquery.select(cb.literal(1));
	    
	    query.where(cb.equal(pasta.get(Pasta_.processo), processo), cb.exists(subquery));
	    query.orderBy(cb.asc(pasta.get(Pasta_.nome)));
	    return getEntityManager().createQuery(query).getResultList();
	}
}
