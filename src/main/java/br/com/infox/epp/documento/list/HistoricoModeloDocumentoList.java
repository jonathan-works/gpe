package br.com.infox.epp.documento.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.documento.entity.HistoricoModeloDocumento;
import br.com.infox.seam.util.ComponentUtil;

@Name(HistoricoModeloDocumentoList.NAME)
@Scope(ScopeType.CONVERSATION)
public class HistoricoModeloDocumentoList extends EntityList<HistoricoModeloDocumento>{
	
	public static final String NAME = "historicoModeloDocumentoList";

	private static final long serialVersionUID = 1L;
	
	private static final String TEMPLATE = "/ModeloDocumento/historicoModeloDocumentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "historicosModelosDocumento.xls";
	
	private static final String DEFAULT_EJBQL = "select o from HistoricoModeloDocumento o";
	private static final String DEFAULT_ORDER = "dataAlteracao DESC";
	
	@Override
	protected void addSearchFields() {
		addSearchField("modeloDocumento", SearchCriteria.IGUAL);
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
    
}
