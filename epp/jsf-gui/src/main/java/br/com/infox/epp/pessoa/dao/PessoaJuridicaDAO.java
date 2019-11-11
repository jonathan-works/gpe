package br.com.infox.epp.pessoa.dao;

import static br.com.infox.epp.pessoa.query.PessoaJuridicaQuery.CNPJ_PARAM;
import static br.com.infox.epp.pessoa.query.PessoaJuridicaQuery.SEARCH_BY_CNPJ;

import java.util.HashMap;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;

@Stateless
@AutoCreate
@Name(PessoaJuridicaDAO.NAME)
public class PessoaJuridicaDAO extends DAO<PessoaJuridica> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pessoaJuridicaDAO";

    public PessoaJuridica searchByCnpj(final String cnpj) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(CNPJ_PARAM, cnpj);
        return getNamedSingleResult(SEARCH_BY_CNPJ, parameters);
    }

}
