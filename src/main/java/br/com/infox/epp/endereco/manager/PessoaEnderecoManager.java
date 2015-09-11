package br.com.infox.epp.endereco.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.endereco.dao.PessoaEnderecoDAO;
import br.com.infox.epp.endereco.entity.PessoaEndereco;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Name(PessoaEnderecoManager.NAME)
@AutoCreate
public class PessoaEnderecoManager extends Manager<PessoaEnderecoDAO, PessoaEndereco> {
    public static final String NAME = "pessoaEnderecoManager";
    private static final long serialVersionUID = 1L;
    
    public PessoaEndereco getByPessoa(Pessoa pessoa) {
        return getDao().getByPessoa(pessoa);
    }

    public void removeByPessoa(Pessoa pessoa) throws DAOException {
        remove(getByPessoa(pessoa));
    }

}
