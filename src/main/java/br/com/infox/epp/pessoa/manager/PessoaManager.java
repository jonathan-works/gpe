package br.com.infox.epp.pessoa.manager;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Events;

import br.com.infox.core.manager.Manager;
import br.com.infox.epp.pessoa.dao.PessoaDAO;
import br.com.infox.epp.pessoa.dao.PessoaFisicaDAO;
import br.com.infox.epp.pessoa.dao.PessoaJuridicaDAO;
import br.com.infox.epp.pessoa.entity.Pessoa;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;

@Name(PessoaManager.NAME)
@AutoCreate
public class PessoaManager extends Manager<PessoaDAO, Pessoa> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pessoaManager";

    @In private PessoaFisicaDAO pessoaFisicaDAO;
    @In private PessoaJuridicaDAO pessoaJuridicaDAO;

    public void carregaPessoa(final TipoPessoaEnum tipoPessoa,
            final String codigo) {
        final Events events = Events.instance();

        if (TipoPessoaEnum.F.equals(tipoPessoa)) {
            events.raiseEvent(PessoaFisica.EVENT_LOAD, pessoaFisicaDAO.searchByCpf(codigo));
        } else if (TipoPessoaEnum.J.equals(tipoPessoa)) {
            events.raiseEvent(PessoaJuridica.EVENT_LOAD, pessoaJuridicaDAO.searchByCnpj(codigo));
        }
    }
}
