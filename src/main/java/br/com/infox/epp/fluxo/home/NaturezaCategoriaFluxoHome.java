package br.com.infox.epp.fluxo.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Erik Liberal
 *
 */
@Name(NaturezaCategoriaFluxoHome.NAME)
@Scope(ScopeType.CONVERSATION)
@Deprecated
public class NaturezaCategoriaFluxoHome extends AbstractHome<NaturezaCategoriaFluxo> {
	private static final long serialVersionUID = 1L;

	public static final String NAME = "naturezaCategoriaFluxoHome";

}