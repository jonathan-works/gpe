package br.com.infox.epp.access.list;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class LocalizacaoComEstruturaList extends EntityList<Localizacao> {

    private static final long serialVersionUID = 1L;
    public static final String DEFAULT_EJBQL = "select o from Localizacao o";
    public static final String DEFAULT_ORDER = "caminhoCompleto";
    
    @Override
    protected void addSearchFields() {
        addSearchField("estruturaPai", SearchCriteria.IGUAL);
        addSearchField("caminhoCompleto", SearchCriteria.INICIANDO);
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
    protected void setCustomFilters() {
        Localizacao localizacao = Authenticator.getLocalizacaoAtual();
        if (localizacao.getEstruturaPai() != null) {
            getEntity().setCaminhoCompleto(Authenticator.getLocalizacaoAtual().getCaminhoCompleto());
        }
    }
    
    @Override
    protected Map<String, String> getCustomColumnsOrder() {
        return null;
    }
}
