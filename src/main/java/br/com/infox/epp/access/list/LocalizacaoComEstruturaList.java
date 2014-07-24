package br.com.infox.epp.access.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;

@Name(LocalizacaoComEstruturaList.NAME)
@AutoCreate
@Scope(ScopeType.PAGE)
public class LocalizacaoComEstruturaList extends EntityList<Localizacao> {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "localizacaoComEstruturaList";
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
