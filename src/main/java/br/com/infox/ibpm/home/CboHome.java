package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.cliente.entity.Cbo;
import br.com.itx.component.AbstractHome;

@Name(CboHome.NAME)
@Scope(ScopeType.PAGE)
public class CboHome extends AbstractHome<Cbo>{

	public static final String NAME = "cboHome";
	private static final long serialVersionUID = 1L;
	
}
