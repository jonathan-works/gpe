package br.com.infox.epp.pessoa.dao;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Name(PessoaDAO.NAME)
@AutoCreate
public class PessoaDAO extends DAO<Pessoa> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "pessoaDAO";

}
