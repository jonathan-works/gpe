package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.epp.pessoa.entity.PessoaJuridica;
import br.com.itx.util.ComponentUtil;

@Name(PessoaJuridicaList.NAME)
@Scope(ScopeType.PAGE)
public class PessoaJuridicaList extends EntityList<PessoaJuridica> {

	public static final String NAME = "pessoaJuridicaList";

	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/PessoaJuridica/pessoaJuridicaTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "PessoaJuridica.xls";
	
	private static final String DEFAULT_EJBQL = "select o from PessoaJuridica o";
	private static final String DEFAULT_ORDER = "nome";
	
	@Override
	protected void addSearchFields() {
		addSearchField("cnpj", SearchCriteria.IGUAL);
		addSearchField("nome", SearchCriteria.CONTENDO);
		addSearchField("razaoSocial", SearchCriteria.CONTENDO);
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
	
	public static PessoaJuridicaList instance(){
		return ComponentUtil.getComponent(PessoaJuridicaList.NAME);
	}
	
	@Override
    public EntityList<PessoaJuridica> getBeanList() {
        return PessoaJuridicaList.instance();
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
