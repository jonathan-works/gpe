package br.com.infox.epp.pessoa.dao;

import static br.com.infox.epp.pessoa.query.PessoaFisicaQuery.CPF_PARAM;
import static br.com.infox.epp.pessoa.query.PessoaFisicaQuery.LOCALIZACAO_PARAM;
import static br.com.infox.epp.pessoa.query.PessoaFisicaQuery.SEARCH_BY_CPF;
import static br.com.infox.epp.pessoa.query.PessoaFisicaQuery.SEARCH_BY_CPF_AND_IS_USUARIO_AND_LOCALIZACAO;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Name(PessoaFisicaDAO.NAME)
@AutoCreate
public class PessoaFisicaDAO extends DAO<PessoaFisica> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pessoaFisicaDAO";

    public PessoaFisica searchByCpf(final String cpf) {
        final HashMap<String, Object> parameters = new HashMap<>();
        parameters.put(CPF_PARAM, cpf);
        return getNamedSingleResult(SEARCH_BY_CPF, parameters);
    }
    
    public PessoaFisica searchByCpfAndIsUsuarioAndLocalizacao(String cpf, Localizacao localizacao){
    	Map<String, Object> parameters = new HashMap<>();
        parameters.put(CPF_PARAM, cpf);
        parameters.put(LOCALIZACAO_PARAM, localizacao);
        return getNamedSingleResult(SEARCH_BY_CPF_AND_IS_USUARIO_AND_LOCALIZACAO, parameters);
    }

}
