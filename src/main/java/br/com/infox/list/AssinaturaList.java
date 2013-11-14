package br.com.infox.list;

import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.processo.documento.entity.ProcessoDocumento;

@Name(AssinaturaList.NAME)
@Scope(ScopeType.PAGE)
public class AssinaturaList extends EntityList<ProcessoDocumento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "assinaturaList";

	private static final String DEFAULT_EJBQL = "select o from ProcessoDocumento o where "
												+ "o.processoDocumentoBin.certChain is not null";
	private static final String DEFAULT_ORDER = "processoDocumentoBin.dataInclusao";
	private static final String R1 = "idProcessoDocumento = #{processoDocumentoHome.instance.idProcessoDocumento}";

	@Override
	protected void addSearchFields() {
		addSearchField("idProcessoDocumento", SearchCriteria.IGUAL, R1);
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
