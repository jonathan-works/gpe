package br.com.infox.epp.pessoaJuridica;

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
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PessoaJuridicaSearch {

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public PessoaJuridica getPessoaJuridicaByCnpj(String cnpj) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PessoaJuridica> cq = cb.createQuery(PessoaJuridica.class);
		Root<PessoaJuridica> pj = cq.from(PessoaJuridica.class);
		Predicate cnpjIgual = cb.equal(pj.get(PessoaJuridica_.cnpj), cnpj);
		Predicate ativo = cb.isTrue(pj.get(PessoaJuridica_.ativo));
		cq = cq.select(pj).where(cb.and(cnpjIgual, ativo));
		List<PessoaJuridica> pessoas = getEntityManager().createQuery(cq).setMaxResults(1).getResultList();
		return pessoas == null || pessoas.isEmpty() ? null : pessoas.get(0);
	}

}
