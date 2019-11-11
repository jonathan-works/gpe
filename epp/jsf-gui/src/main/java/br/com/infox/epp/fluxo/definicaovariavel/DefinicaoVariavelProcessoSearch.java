package br.com.infox.epp.fluxo.definicaovariavel;

import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class DefinicaoVariavelProcessoSearch {
	
	public List<DefinicaoVariavelProcesso> listVariaveisByFluxo(Fluxo fluxo) {
		EntityManager entityManager = EntityManagerProducer.getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DefinicaoVariavelProcesso> query = cb.createQuery(DefinicaoVariavelProcesso.class);
		Root<DefinicaoVariavelProcesso> root = query.from(DefinicaoVariavelProcesso.class);
		query.where(cb.equal(root.get(DefinicaoVariavelProcesso_.fluxo), fluxo));
		query.orderBy(cb.asc(root.get(DefinicaoVariavelProcesso_.label)));
		return entityManager.createQuery(query).getResultList();
	}
	
	public DefinicaoVariavelProcesso getDefinicao(Fluxo fluxo, String nomeVariavel) {
		EntityManager entityManager = EntityManagerProducer.getEntityManager();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<DefinicaoVariavelProcesso> query = cb.createQuery(DefinicaoVariavelProcesso.class);
		Root<DefinicaoVariavelProcesso> root = query.from(DefinicaoVariavelProcesso.class);
		query.where(cb.equal(root.get(DefinicaoVariavelProcesso_.fluxo), fluxo), cb.equal(root.get(DefinicaoVariavelProcesso_.nome), nomeVariavel));
		try {
			return entityManager.createQuery(query).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public List<DefinicaoVariavelProcesso> getDefinicoesVariaveis(Fluxo fluxo, String recursoVariavel, boolean usuarioExterno) {
    	CriteriaBuilder cb = EntityManagerProducer.getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<DefinicaoVariavelProcesso> query = cb.createQuery(DefinicaoVariavelProcesso.class);
    	Root<DefinicaoVariavelProcessoRecurso> root = query.from(DefinicaoVariavelProcessoRecurso.class);
    	Join<DefinicaoVariavelProcessoRecurso, DefinicaoVariavelProcesso> definicao = root.join(DefinicaoVariavelProcessoRecurso_.definicaoVariavelProcesso, JoinType.INNER);
    	query.select(definicao);
    	query.orderBy(cb.asc(root.get(DefinicaoVariavelProcessoRecurso_.ordem)));
    	
    	query.where(cb.equal(definicao.get(DefinicaoVariavelProcesso_.fluxo), fluxo), cb.equal(root.get(DefinicaoVariavelProcessoRecurso_.recurso), recursoVariavel));
    	if (usuarioExterno) {
    		query.where(query.getRestriction(), cb.isTrue(root.get(DefinicaoVariavelProcessoRecurso_.visivelUsuarioExterno)));
    	}
    	return EntityManagerProducer.getEntityManager().createQuery(query).getResultList();
    }
    
    public DefinicaoVariavelProcessoRecurso getDefinicaoVariavelRecurso(DefinicaoVariavelProcesso definicaoVariavelProcesso, String recursoVariavel) {
    	CriteriaBuilder cb = EntityManagerProducer.getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<DefinicaoVariavelProcessoRecurso> query = cb.createQuery(DefinicaoVariavelProcessoRecurso.class);
    	Root<DefinicaoVariavelProcessoRecurso> root = query.from(DefinicaoVariavelProcessoRecurso.class);
    	Join<DefinicaoVariavelProcessoRecurso, DefinicaoVariavelProcesso> definicao = root.join(DefinicaoVariavelProcessoRecurso_.definicaoVariavelProcesso, JoinType.INNER);
    	
    	query.where(cb.equal(definicao, definicaoVariavelProcesso), cb.equal(root.get(DefinicaoVariavelProcessoRecurso_.recurso), recursoVariavel));
    	try {
    		return EntityManagerProducer.getEntityManager().createQuery(query).getSingleResult();
    	} catch (NoResultException e) {
    		return null;
    	}
    }
    
    public List<DefinicaoVariavelProcessoRecurso> getRecursos(DefinicaoVariavelProcesso definicaoVariavelProcesso) {
    	CriteriaBuilder cb = EntityManagerProducer.getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<DefinicaoVariavelProcessoRecurso> query = cb.createQuery(DefinicaoVariavelProcessoRecurso.class);
    	Root<DefinicaoVariavelProcessoRecurso> root = query.from(DefinicaoVariavelProcessoRecurso.class);
    	query.where(cb.equal(root.get(DefinicaoVariavelProcessoRecurso_.definicaoVariavelProcesso), definicaoVariavelProcesso));
    	return EntityManagerProducer.getEntityManager().createQuery(query).getResultList();
    }
    
	public List<DefinicaoVariavelProcessoRecurso> getDefinicoesVariaveis(Fluxo fluxo, String recursoVariavel) {
		CriteriaBuilder cb = EntityManagerProducer.getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<DefinicaoVariavelProcessoRecurso> query = cb.createQuery(DefinicaoVariavelProcessoRecurso.class);
    	Root<DefinicaoVariavelProcessoRecurso> root = query.from(DefinicaoVariavelProcessoRecurso.class);
    	Join<DefinicaoVariavelProcessoRecurso, DefinicaoVariavelProcesso> definicao = root.join(DefinicaoVariavelProcessoRecurso_.definicaoVariavelProcesso, JoinType.INNER);
    	//query.select(definicao);
    	query.orderBy(cb.asc(root.get(DefinicaoVariavelProcessoRecurso_.ordem)));
    	query.where(cb.equal(definicao.get(DefinicaoVariavelProcesso_.fluxo), fluxo), cb.equal(root.get(DefinicaoVariavelProcessoRecurso_.recurso), recursoVariavel));
    	return EntityManagerProducer.getEntityManager().createQuery(query).getResultList();
    }
}
