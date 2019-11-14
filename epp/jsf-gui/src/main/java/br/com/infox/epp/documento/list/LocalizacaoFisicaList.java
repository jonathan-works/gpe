package br.com.infox.epp.documento.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;

@Named
@ViewScoped
public class LocalizacaoFisicaList extends EntityList<LocalizacaoFisica> {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/LocalizacaoFisica/LocalizacaoFisicaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "LocalizacaoFisica.xls";

    private static final String DEFAULT_EJBQL = "select o from LocalizacaoFisica o";
    private static final String DEFAULT_ORDER = "caminhoCompleto";

    private static final String R1 = "o.caminhoCompleto like concat("
            + "#{localizacaoFisicaList.entity.localizacaoFisicaPai.caminhoCompleto}, '%')";

    @Override
    protected void addSearchFields() {
        addSearchField("descricao", SearchCriteria.CONTENDO);
        addSearchField("localizacaoFisicaPai", SearchCriteria.CONTENDO, R1);
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
