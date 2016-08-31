package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;

@Name(TipoComunicacaoList.NAME)
@Scope(ScopeType.PAGE)
@AutoCreate
public class TipoComunicacaoList extends EntityList<TipoComunicacao> {
    private static final long serialVersionUID = 1L;
    public static final String NAME = "tipoComunicacaoList";

    private static final String DEFAULT_EJBQL = "select o from TipoComunicacao o";
    private static final String DEFAULT_ORDER = "descricao";
    
    @Override
    public void newInstance() {
    	super.newInstance();
    	getEntity().setTipoUsoComunicacao(null);
    }
    
    @Override
    protected void addSearchFields() {
        addSearchField("descricao", SearchCriteria.CONTENDO);
        addSearchField("codigo", SearchCriteria.CONTENDO);
        addSearchField("tipoUsoComunicacao", SearchCriteria.IGUAL);
        addSearchField("ativo", SearchCriteria.IGUAL);
        addSearchField("classificacaoDocumento", SearchCriteria.IGUAL);
        addSearchField("tipoModeloDocumento", SearchCriteria.IGUAL);
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
