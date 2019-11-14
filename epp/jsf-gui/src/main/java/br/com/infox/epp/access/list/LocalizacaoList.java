package br.com.infox.epp.access.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.util.Beans;

@Named
@ViewScoped
public class LocalizacaoList extends EntityList<Localizacao> {

    private static final long serialVersionUID = 1L;

    private static final String TEMPLATE = "/Localizacao/localizacaoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "Localizacoes.xls";

    private static final String DEFAULT_EJBQL = "select o from Localizacao o where o.estruturaPai is null";
    private static final String DEFAULT_ORDER = "caminhoCompleto";

    @Override
    protected void addSearchFields() {
        addSearchField("localizacao", SearchCriteria.CONTENDO);
        addSearchField("localizacaoPai", SearchCriteria.IGUAL);
        addSearchField("ativo", SearchCriteria.IGUAL);
        addSearchField("hierarchicalPath", SearchCriteria.INICIANDO, "o.caminhoCompleto like concat(#{authenticator.getLocalizacaoAtual().caminhoCompleto},'%')");
    }
    
    @Override
    public void newInstance() {
    	super.newInstance();
    	Beans.getReference(LocalizacaoTreeHandler.class).clearTree();
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
