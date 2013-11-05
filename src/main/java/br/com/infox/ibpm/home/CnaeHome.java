package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import br.com.infox.ibpm.entity.Cnae;
import br.com.itx.component.AbstractHome;

@Name(CnaeHome.NAME)
@Scope(ScopeType.PAGE)
public class CnaeHome extends AbstractHome<Cnae>{
	
    public static final String NAME = "cnaeHome";
	private static final long serialVersionUID = 1L;
	
}
