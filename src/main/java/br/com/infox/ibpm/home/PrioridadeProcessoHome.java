package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.epp.processo.prioridade.entity.PrioridadeProcesso;
import br.com.itx.component.AbstractHome;

@Name(PrioridadeProcessoHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class PrioridadeProcessoHome extends AbstractHome<PrioridadeProcesso>{
	public static final String NAME = "prioridadeProcessoHome";
	private static final long serialVersionUID = 1L;
}
