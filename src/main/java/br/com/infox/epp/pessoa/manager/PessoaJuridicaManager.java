package br.com.infox.epp.pessoa.manager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;

@Name(PessoaJuridicaManager.NAME)
@Scope(ScopeType.EVENT)
@AutoCreate
public class PessoaJuridicaManager extends Manager<PessoaJuridicaDAO, PessoaJuridica> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "pessoaJuridicaManager";
}
