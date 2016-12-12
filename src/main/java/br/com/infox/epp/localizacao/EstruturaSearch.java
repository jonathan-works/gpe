package br.com.infox.epp.localizacao;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.core.persistence.PersistenceController;
import br.com.infox.epp.access.entity.Estrutura;
import br.com.infox.epp.access.entity.Estrutura_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class EstruturaSearch extends PersistenceController {

	public Estrutura getEstruturaByNome(String codigoEstrutura) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Estrutura> cq = cb.createQuery(Estrutura.class);
		Root<Estrutura> estrutura = cq.from(Estrutura.class);
		cq = cq.select(estrutura).where(cb.equal(estrutura.get(Estrutura_.nome), codigoEstrutura));
		return getEntityManager().createQuery(cq).getSingleResult();
	}
	
    public List<Estrutura> getEstruturaByParteNome(String nome, Integer maxResult) {
        CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Estrutura> cq = cb.createQuery(Estrutura.class);
        Root<Estrutura> estrutura = cq.from(Estrutura.class);
        cq.where(cb.like(cb.lower(estrutura.get(Estrutura_.nome)), cb.lower(cb.literal("%" + nome + "%"))));
        return getEntityManager().createQuery(cq).setMaxResults(maxResult).getResultList();
    }

}
