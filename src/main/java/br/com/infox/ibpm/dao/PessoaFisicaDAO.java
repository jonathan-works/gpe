package br.com.infox.ibpm.dao;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.PessoaFisica;

public class PessoaFisicaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaFisicaDAO";
	
	public PessoaFisica searchByCpf(String cpf){
		String hql = "select o from PessoaFisica o where o.cpf = :cpf";
		return (PessoaFisica) entityManager.createQuery(hql)
				.setParameter("cpf", cpf).getSingleResult();
	}

}
