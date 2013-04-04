package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.ibpm.entity.ParteProcesso;
import br.com.itx.component.AbstractHome;

@Name(ParteProcessoHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class ParteProcessoHome extends AbstractHome<ParteProcesso>{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "parteProcessoHome";
	
}
