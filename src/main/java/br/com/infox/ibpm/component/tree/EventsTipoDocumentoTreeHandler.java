package br.com.infox.ibpm.component.tree;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

@Name(EventsTipoDocumentoTreeHandler.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class EventsTipoDocumentoTreeHandler extends AutomaticEventsTreeHandler{

	private static final long serialVersionUID = 1L;
	public static final String NAME = "eventsTipoDocumentoTree";

	public static EventsTipoDocumentoTreeHandler instance() {
		return (EventsTipoDocumentoTreeHandler) org.jboss.seam.Component.getInstance(EventsTipoDocumentoTreeHandler.NAME);
	}
	
}
