package br.com.infox.epa.action.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epa.entity.Natureza;
import br.com.infox.epa.list.NaturezaList;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(NaturezaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaAction extends AbstractHome<Natureza> {

	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/Natureza/NaturezaTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Naturezas.xls";
	
	public static final String NAME = "naturezaAction";
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}
	
	@Override
	public EntityList<Natureza> getBeanList() {
		return NaturezaList.instance();
	}

}