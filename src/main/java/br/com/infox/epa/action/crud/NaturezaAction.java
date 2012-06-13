package br.com.infox.epa.action.crud;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epa.entity.Natureza;
import br.com.itx.component.AbstractHome;

/**
 * 
 * @author Daniel
 *
 */
@Name(NaturezaAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class NaturezaAction extends AbstractHome<Natureza> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "naturezaAction";

}