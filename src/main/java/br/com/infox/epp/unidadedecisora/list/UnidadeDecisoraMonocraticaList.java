package br.com.infox.epp.unidadedecisora.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.seam.util.ComponentUtil;

@Name(UnidadeDecisoraMonocraticaList.NAME)
@Scope(ScopeType.PAGE)
public class UnidadeDecisoraMonocraticaList extends EntityList<UnidadeDecisoraMonocratica> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraMonocraticaList";
	
	private static final String TEMPLATE = "/UnidadeDecisoraMonocratica/unidadeDecisoraMonocraticaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "UnidadeDecisoraMonocratica.xls";
	
	private static final String DEFAULT_EJBQL = "select o from UnidadeDecisoraMonocratica o";
    private static final String DEFAULT_ORDER = "nome";
    
    public Localizacao getLocalizacao() {
        return getEntity().getLocalizacao();
    }
    
    public void setLocalizacao(Localizacao localizacao) {
        if (localizacao == null || localizacao.getEstruturaFilho() != null) {
            getEntity().setLocalizacao(localizacao);
        } 
    }
    
    @Override
    public void newInstance() {
    	super.newInstance();
    	LocalizacaoTreeHandler ut = ComponentUtil.getComponent(LocalizacaoTreeHandler.NAME);
    	ut.clearTree();
    }
    
	@Override
	protected void addSearchFields() {
		addSearchField("nome", SearchCriteria.CONTENDO);
		addSearchField("localizacao", SearchCriteria.IGUAL);
		addSearchField("ativo", SearchCriteria.IGUAL);
		addSearchField("recebeDistribuicao", SearchCriteria.IGUAL);
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
