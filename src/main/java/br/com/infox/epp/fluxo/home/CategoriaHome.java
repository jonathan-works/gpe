package br.com.infox.epp.fluxo.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(CategoriaHome.NAME)
@Scope(ScopeType.CONVERSATION)
@Deprecated
public class CategoriaHome extends AbstractHome<Categoria> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "categoriaHome";
	
}