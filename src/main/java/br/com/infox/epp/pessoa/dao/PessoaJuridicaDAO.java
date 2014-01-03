package br.com.infox.epp.pessoa.dao;

import java.util.HashMap;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;

@Name(PessoaJuridicaDAO.NAME)
@AutoCreate
public class PessoaJuridicaDAO extends GenericDAO {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaJuridicaDAO";
	
	public PessoaJuridica searchByCnpj(final String cnpj){
		final String hql = "select o from PessoaJuridica o where o.cnpj = :cnpj";
		final HashMap<String,Object> parameters = new HashMap<>();
		parameters.put("cnpj", cnpj);
		return getSingleResult(hql, parameters);
	}

}
