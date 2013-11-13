package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.itx.util.ComponentUtil;

@Name(PessoaFisicaList.NAME)
@Scope(ScopeType.PAGE)
public class PessoaFisicaList extends EntityList<PessoaFisica>{
	
	public static final String NAME = "pessoaFisicaList";

	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/PessoaFisica/pessoaFisicaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "PessoaFisica.xls";
	
	private static final String DEFAULT_EJBQL = "select o from PessoaFisica o";
	private static final String DEFAULT_ORDER = "nome";

	@Override
	protected void addSearchFields() {
		addSearchField("cpf", SearchCriteria.CONTENDO);
		addSearchField("nome", SearchCriteria.CONTENDO);
		addSearchField("dataNascimento", SearchCriteria.DATA_IGUAL);
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
	
	public static PessoaFisicaList instance() {
		return ComponentUtil.getComponent(PessoaFisicaList.NAME);
	}
	
	@Override
    public EntityList<PessoaFisica> getBeanList() {
        return PessoaFisicaList.instance();
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
