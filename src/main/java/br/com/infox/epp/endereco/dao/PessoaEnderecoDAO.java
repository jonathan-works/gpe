package br.com.infox.epp.endereco.dao;

import static br.com.infox.epp.endereco.query.PessoaEnderecoQuery.PARAM_PESSOA;
import static br.com.infox.epp.endereco.query.PessoaEnderecoQuery.PESSOA_ENDERECO_BY_PESSOA;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.dao.DAO;
import br.com.infox.epp.endereco.entity.PessoaEndereco;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Name(PessoaEnderecoDAO.NAME)
@AutoCreate
public class PessoaEnderecoDAO extends DAO<PessoaEndereco> {
    static final String NAME = "pessoaEnderecoDAO";
    private static final long serialVersionUID = 1L;
    
    public PessoaEndereco getByPessoa(Pessoa pessoa) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(PARAM_PESSOA, pessoa);
        return getNamedSingleResult(PESSOA_ENDERECO_BY_PESSOA, params);
    }

}
