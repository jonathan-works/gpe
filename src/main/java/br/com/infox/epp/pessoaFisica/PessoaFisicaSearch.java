package br.com.infox.epp.pessoaFisica;

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
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaFisica_;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PessoaFisicaSearch {

	private EntityManager getEntityManager() {
		return EntityManagerProducer.getEntityManager();
	}

	public PessoaFisica getByCpf(String cpf) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<PessoaFisica> cq = cb.createQuery(PessoaFisica.class);
		
		Root<PessoaFisica> pessoa = cq.from(PessoaFisica.class);
		
		Predicate ativo = cb.isTrue(pessoa.get(PessoaFisica_.ativo));
		Predicate cpfIgual = cb.equal(pessoa.get(PessoaFisica_.cpf), cpf);
		
		cq = cq.select(pessoa).where(cb.and(ativo, cpfIgual));
		List<PessoaFisica> pessoas =getEntityManager().createQuery(cq).setMaxResults(1).getResultList();
		return pessoas == null || pessoas.isEmpty() ? null : pessoas.get(0);
	}

}
