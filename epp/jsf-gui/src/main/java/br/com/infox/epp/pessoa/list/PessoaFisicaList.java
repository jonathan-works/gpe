package br.com.infox.epp.pessoa.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.PessoaFisica;

@Named
@ViewScoped
public class PessoaFisicaList extends EntityList<PessoaFisica> {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/PessoaFisica/pessoaFisicaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "PessoaFisica.xls";

    private static final String DEFAULT_EJBQL = "select o from PessoaFisica o";
    private static final String DEFAULT_ORDER = "nome";

    @Override
    protected void addSearchFields() {
        addSearchField("cpf", SearchCriteria.CONTENDO);
        addSearchField("nome", SearchCriteria.CONTENDO);
        addSearchField("dataNascimento", SearchCriteria.DATA_IGUAL);
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

    @Override
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }
}
