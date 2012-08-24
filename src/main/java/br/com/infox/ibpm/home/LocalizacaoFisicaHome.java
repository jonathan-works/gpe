package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.ibpm.entity.LocalizacaoFisica;
import br.com.itx.component.AbstractHome;

@Name(LocalizacaoFisicaHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class LocalizacaoFisicaHome extends AbstractHome<LocalizacaoFisica>{

	public static final String NAME = "localizacaoFisicaHome";
	private static final long serialVersionUID = 1L;
	
}
