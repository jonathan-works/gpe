package br.com.infox.epp.pessoa.manager;

import javax.ejb.Stateless;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;

@Name(PessoaJuridicaManager.NAME)
@AutoCreate
@Stateless
public class PessoaJuridicaManager extends Manager<PessoaJuridicaDAO, PessoaJuridica> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pessoaJuridicaManager";

    public PessoaJuridica getByCnpj(final String cnpj) {
        return getDao().searchByCnpj(cnpj);
    }

}
