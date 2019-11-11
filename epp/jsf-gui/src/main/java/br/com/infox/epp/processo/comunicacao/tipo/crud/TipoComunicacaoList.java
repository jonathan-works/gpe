package br.com.infox.epp.processo.comunicacao.tipo.crud;

import java.util.Map;

import javax.inject.Named;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.cdi.ViewScoped;

@Named
@ViewScoped
public class TipoComunicacaoList extends EntityList<TipoComunicacao> {
    private static final long serialVersionUID = 1L;

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
