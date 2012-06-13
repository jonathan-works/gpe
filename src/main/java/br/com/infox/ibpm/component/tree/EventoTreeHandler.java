package br.com.infox.ibpm.component.tree;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.infox.ibpm.entity.Evento;
import br.com.itx.util.ComponentUtil;

@Name(EventoTreeHandler.NAME)
@BypassInterceptors
public class EventoTreeHandler extends AbstractTreeHandler<Evento> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "eventoTree";
	public static final String SELECT_GRUPO_EVENTO_OBS = "selectEventoTree";	
	
	@Override
	protected String getQueryRoots() {
		return "select n from Evento n "
			 + "where eventoSuperior is null "
			 + "order by evento";
	}

	@Override
	protected String getQueryChildren() {
		return "select n from Evento n where eventoSuperior = :"
			  + EntityNode.PARENT_NODE;
	}

	@Override
	protected String getEventSelected() { 
		return SELECT_GRUPO_EVENTO_OBS;
	}
	
	@Override
	protected Evento getEntityToIgnore() {
		return ComponentUtil.getInstance("eventoHome");
	}
}