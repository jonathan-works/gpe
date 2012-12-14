package br.com.infox.ibpm.home;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.entity.ListaEmail;
import br.com.itx.component.AbstractHome;
import br.com.itx.util.ComponentUtil;

@Name(ListaEmailHome.NAME)
@BypassInterceptors
public class ListaEmailHome extends AbstractHome<ListaEmail> {
	private static final long serialVersionUID = 1L;
	public static final String NAME = "listaEmailHome";
	
	public static final ListaEmailHome instance() {
		return ComponentUtil.getComponent(NAME);
	}
	
}
