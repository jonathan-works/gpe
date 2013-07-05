package br.com.infox.ibpm.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.ibpm.entity.PessoaFisica;

@Name(PessoaJuridicaDAO.NAME)
@AutoCreate
public class PessoaJuridicaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaJuridicaDAO";
	
	public PessoaFisica searchByCnpj(String cnpj){
		String hql = "select o from PessoaJuridica o where o.cnpj = :cnpj";
		return (PessoaFisica) entityManager.createQuery(hql)
				.setParameter("cnpj", cnpj).getSingleResult();
	}

}
