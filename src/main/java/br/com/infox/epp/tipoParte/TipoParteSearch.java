package br.com.infox.epp.tipoParte;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.documento.entity.ModeloDocumento;
import br.com.infox.epp.documento.entity.ModeloDocumento_;
import br.com.infox.epp.processo.partes.entity.TipoParte;
import br.com.infox.epp.processo.partes.entity.TipoParte_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class TipoParteSearch {

	public TipoParte getTipoParteByIdentificador(String identificador) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<TipoParte> cq = cb.createQuery(TipoParte.class);
		Root<TipoParte> tipoParte = cq.from(TipoParte.class);
		Predicate identificadorIgual = cb.equal(tipoParte.get(TipoParte_.identificador), identificador);
		cq.select(tipoParte).where(identificadorIgual);
		return getEntityManager().createQuery(cq).getSingleResult();
	}
	
	public List<TipoParte> findAll() {
	    CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<TipoParte> cq = cb.createQuery(TipoParte.class);
        Root<TipoParte> tipoParte = cq.from(TipoParte.class);
        cq.select(tipoParte);
        cq.orderBy(cb.asc(tipoParte.get(TipoParte_.descricao)));
        return getEntityManager().createQuery(cq).getResultList();
	}

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

    public List<TipoParte> findTipoParteWithDescricaoLike(String pattern) {
        EntityManager em = EntityManagerProducer.getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<TipoParte> cq = cb.createQuery(TipoParte.class);
        Root<TipoParte> tipoParte = cq.from(TipoParte.class);

        cq = cq.select(tipoParte).where(cb.like(cb.lower(tipoParte.get(TipoParte_.descricao)),
                cb.lower(cb.literal("%" + pattern.toLowerCase() + "%"))));

        return em.createQuery(cq).getResultList();
    }

}
