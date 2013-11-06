package br.com.infox.epp.action.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.entity.Natureza;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(NaturezaHome.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaHome extends AbstractHome<Natureza> {

	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "naturezaHome";
	
}