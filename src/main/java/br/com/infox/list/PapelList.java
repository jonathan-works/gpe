package br.com.infox.list;

import java.util.Map;

import br.com.infox.access.entity.Papel;
import br.com.infox.core.action.list.EntityList;
import br.com.infox.core.action.list.SearchCriteria;

public class PapelList extends EntityList<Papel> {
	
	public static final String NAME = "papelList";
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_EJBQL = "select o from Papel o";
	public static final String DEFAULT_ORDER = "o.nome";
	
	private static final String R1 = "o.idPapel not in (select p.papel.idPapel " +
			"from TipoModeloDocumentoPapel p " +
			"where p.tipoModeloDocumento = #{tipoModeloDocumentoHome.instance})";

	@Override
	protected void addSearchFields() {
		addSearchField("idPapel", SearchCriteria.igual, R1);
	}

	@Override
	protected String getDefaultEjbql() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getDefaultOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		// TODO Auto-generated method stub
		return null;
	}

}
