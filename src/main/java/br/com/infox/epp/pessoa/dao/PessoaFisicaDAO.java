package br.com.infox.epp.pessoa.dao;

import java.util.HashMap;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Name(PessoaFisicaDAO.NAME)
@AutoCreate
public class PessoaFisicaDAO extends DAO<PessoaFisica> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaFisicaDAO";
	
	public PessoaFisica searchByCpf(final String cpf){
		final String hql = "select o from PessoaFisica o where o.cpf = :cpf";
		final HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("cpf", cpf);
		return getSingleResult(hql, parameters);
	}

}
