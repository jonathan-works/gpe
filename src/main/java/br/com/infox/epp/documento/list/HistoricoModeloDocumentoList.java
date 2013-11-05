package br.com.infox.epp.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;
import br.com.infox.ibpm.entity.HistoricoModeloDocumento;
import br.com.itx.util.ComponentUtil;

@Name(HistoricoModeloDocumentoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class HistoricoModeloDocumentoList extends EntityList<HistoricoModeloDocumento>{
	
	public static final String NAME = "historicoModeloDocumentoList";

	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/ModeloDocumento/historicoModeloDocumentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "historicosModelosDocumento.xls";
	
	private static final String DEFAULT_EJBQL = "select o from HistoricoModeloDocumento o";
	private static final String DEFAULT_ORDER = "dataAlteracao DESC";
	private static final String R1 = "modeloDocumento = #{modeloDocumentoHome.instance}";
	
	@Override
	protected void addSearchFields() {
		addSearchField("modeloDocumento", SearchCriteria.IGUAL, R1);
		addSearchField("usuarioAlteracao", SearchCriteria.IGUAL);
		addSearchField("dataAlteracao", SearchCriteria.DATA_IGUAL);
		addSearchField("tituloModeloDocumento", SearchCriteria.CONTENDO);
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
	
	public static HistoricoModeloDocumentoList instance() {
	    return ComponentUtil.getComponent(HistoricoModeloDocumentoList.NAME);
	}
	
	@Override
    public String getTemplate(){
        return TEMPLATE;
    }
    
    @Override
    public String getDownloadXlsName(){
        return DOWNLOAD_XLS_NAME;
    }
    
    @Override
    public EntityList<HistoricoModeloDocumento> getBeanList() {
        return HistoricoModeloDocumentoList.instance();
    }
}
