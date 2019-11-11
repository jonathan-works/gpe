package br.com.infox.epp.pessoa.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;

@Named
@ViewScoped
public class PessoaJuridicaList extends EntityList<PessoaJuridica> {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/PessoaJuridica/pessoaJuridicaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "PessoaJuridica.xls";

    private static final String DEFAULT_EJBQL = "select o from PessoaJuridica o";
    private static final String DEFAULT_ORDER = "nome";

    @Override
    protected void addSearchFields() {
        addSearchField("cnpj", SearchCriteria.IGUAL);
        addSearchField("nome", SearchCriteria.CONTENDO);
        addSearchField("razaoSocial", SearchCriteria.CONTENDO);
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
