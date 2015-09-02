package br.com.infox.epp.pessoa.manager;

import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.pessoa.dao.PessoaFisicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Name(PessoaFisicaManager.NAME)
@Scope(ScopeType.EVENT)
@Stateless
@AutoCreate
public class PessoaFisicaManager extends Manager<PessoaFisicaDAO, PessoaFisica> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "pessoaFisicaManager";

    public PessoaFisica getByCpf(final String cpf) {
        return getDao().searchByCpf(cpf);
    }
}
