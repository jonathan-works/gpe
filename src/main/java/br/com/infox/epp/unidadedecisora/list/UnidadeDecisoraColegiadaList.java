package br.com.infox.epp.unidadedecisora.list;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraMonocratica;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraMonocraticaManager;

@Name(UnidadeDecisoraColegiadaList.NAME)
@Scope(ScopeType.CONVERSATION)
@ContextDependency
public class UnidadeDecisoraColegiadaList extends EntityList<UnidadeDecisoraColegiada>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "unidadeDecisoraColegiadaList";
	
	private static final String TEMPLATE = "/UnidadeDecisoraColegiada/unidadeDecisoraColegiadaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "UnidadeDecisoraColegiada.xls";
	
	private static final String DEFAULT_EJBQL = "select o from UnidadeDecisoraColegiada o";
    private static final String DEFAULT_ORDER = "nome";
    
    private static final String FILTRO_UNIDADE_MONOCRATICA = "exists (select 1 from UnidadeDecisoraColegiadaMonocratica udcm " +
    														 "where udcm.unidadeDecisoraMonocratica = #{unidadeDecisoraColegiadaList.unidadeDecisoraMonocratica} " +
    														 "and udcm.unidadeDecisoraColegiada = o) ";
    
    @Inject
    private UnidadeDecisoraMonocraticaManager unidadeDecisoraMonocraticaManager;
    
    private UnidadeDecisoraMonocratica unidadeDecisoraMonocratica;
    
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
    	Beans.getReference(LocalizacaoTreeHandler.class).clearTree();
    	setUnidadeDecisoraMonocratica(null);
    }

	@Override
	protected void addSearchFields() {
		addSearchField("nome", SearchCriteria.CONTENDO);
		addSearchField("unidadeDecisoraColegiadaList.unidadeDecisoraMonocratica", SearchCriteria.NONE, FILTRO_UNIDADE_MONOCRATICA);
		addSearchField("ativo", SearchCriteria.IGUAL);
		addSearchField("localizacao", SearchCriteria.IGUAL);
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

	public UnidadeDecisoraMonocratica getUnidadeDecisoraMonocratica() {
		return unidadeDecisoraMonocratica;
	}

	public void setUnidadeDecisoraMonocratica(UnidadeDecisoraMonocratica unidadeDecisoraMonocratica) {
		this.unidadeDecisoraMonocratica = unidadeDecisoraMonocratica;
	}
	
	public List<UnidadeDecisoraMonocratica> getAllUniDecisoraMonocratica(){
		return unidadeDecisoraMonocraticaManager.findAll();
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
