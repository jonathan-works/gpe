package br.com.infox.epp.access.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class PapelList extends EntityList<Papel> {

    private static final long serialVersionUID = 1L;

    /*
     * Papel e Recurso fazem parte da mesma entidade, a diferença consiste em
     * que o identificador dos recursos começa com '/'
     */
    public static final String DEFAULT_EJBQL = "select o from Papel o where identificador not like '/%'";
    public static final String DEFAULT_ORDER = "o.nome";

    private static final String R1 = "lower(nome) like concat('%',lower(#{papelList.entity.nome}),'%')";
    private static final String R2 = "lower(identificador) like concat('%',lower(#{papelList.entity.identificador}),'%')";
    private static final String TEMPLATE = "/useradmin/PapelReportTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Papel.xls";

    @Override
    protected void addSearchFields() {
        addSearchField("nome", SearchCriteria.CONTENDO, R1);
        addSearchField("identificador", SearchCriteria.CONTENDO, R2);
        addSearchField("termoAdesao", SearchCriteria.IGUAL);
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
