package br.com.infox.epp.pessoa.dao;

import static br.com.infox.epp.pessoa.query.PessoaFisicaQuery.CPF_PARAM;
import static br.com.infox.epp.pessoa.query.PessoaFisicaQuery.SEARCH_BY_CPF;

import java.util.HashMap;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Stateless
@Name(PessoaFisicaDAO.NAME)
@AutoCreate
public class PessoaFisicaDAO extends DAO<PessoaFisica> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pessoaFisicaDAO";

    public PessoaFisica searchByCpf(final String cpf) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(CPF_PARAM, cpf.replaceAll("\\D", ""));
        return getNamedSingleResult(SEARCH_BY_CPF, parameters);
    }
}
