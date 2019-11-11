package br.com.infox.epp.pessoa.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.Pessoa;

@Named
@ViewScoped
public class PessoaList extends EntityList<Pessoa> {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_EJBQL = "select o from Pessoa o";
    private static final String DEFAULT_ORDER = "nome";

    @Override
    protected void addSearchFields() {
        addSearchField("nome", SearchCriteria.CONTENDO);
        addSearchField("tipoPessoa", SearchCriteria.CONTENDO);
        addSearchField("ativo", SearchCriteria.IGUAL);
    }

    @Override
    protected String getDefaultEjbql() {
        return DEFAULT_EJBQL;
    }

    @Override
    protected String getDefaultOrder() {
        return DEFAULT_ORDER;
    }

    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }

}
