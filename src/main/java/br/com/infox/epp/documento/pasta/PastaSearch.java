package br.com.infox.epp.documento.pasta;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.Pasta_;
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
}
