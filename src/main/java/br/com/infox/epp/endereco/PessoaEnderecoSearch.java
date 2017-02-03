package br.com.infox.epp.endereco;


import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Stateless
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
public class PessoaEnderecoSearch {
    
	private EntityManager getEntityManager(){
		return EntityManagerProducer.getEntityManager();
	}
	
    public PessoaEndereco getByPessoa(Pessoa pessoa) {
    	CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
    	CriteriaQuery<PessoaEndereco> query = cb.createQuery(PessoaEndereco.class);
    	Root<PessoaEndereco> pessoaEndereco = query.from(PessoaEndereco.class);
    	query.where(cb.equal(pessoaEndereco.get(PessoaEndereco_.pessoa), pessoa));
    	try {
    		return getEntityManager().createQuery(query).getSingleResult();
    	} catch (NoResultException e) {
    		return null;
		}
    }
}
