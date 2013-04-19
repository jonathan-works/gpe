package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.action.list.EntityList;
import br.com.infox.epp.entity.Categoria;
import br.com.infox.epp.list.CategoriaList;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(CategoriaHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class CategoriaHome extends AbstractHome<Categoria> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "categoriaHome";
	
	public static final String TEMPLATE = "/Categoria/CategoriaTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Categoria.xls";
	
	@Override
	public EntityList<Categoria> getBeanList() {
		return CategoriaList.instance();
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