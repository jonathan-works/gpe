package br.com.infox.ibpm.component.tree;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import br.com.infox.component.tree.EntityNode;
import br.com.infox.ibpm.entity.Evento;
import br.com.infox.ibpm.entity.EventoAgrupamento;

/***
 * Classe para guardar informações sobre os nós raízes se são múltiplos ou não.
 * @author daniel
 *
 */
public class EventsEntityNode extends EntityNode<Evento> {
	
	private static final long serialVersionUID = 1L;
	protected List<EntityNode<Evento>> rootNodes;
	private boolean isMultiplo;
	
	public EventsEntityNode(String queryChildren) {
		super(queryChildren);
	}

	public EventsEntityNode(String[] queryChildren) {
		super(queryChildren);
	}
	
	public EventsEntityNode(EntityNode<Evento> parent, Evento entity, String[] queryChildren) {
		super(parent, entity, queryChildren);
	}

	protected EventsEntityNode createRootNode(EventoAgrupamento ea) {
		EventsEntityNode node = new EventsEntityNode(null, ea.getEvento(), getQueryChildren());
		node.setMultiplo(ea.getMultiplo());
		return node;
	}
	
	@Override
	protected EventsEntityNode createChildNode(Evento n) {
		return new EventsEntityNode(this, n, getQueryChildren());
	}

	@Override
	public List<EntityNode<Evento>> getRoots(Query queryRoots) {
		if (rootNodes == null) {
			rootNodes = new ArrayList<EntityNode<Evento>>();
			List<EventoAgrupamento> roots = queryRoots.getResultList();
			for (EventoAgrupamento ea : roots) {
				if (!ea.equals(getIgnore())) {
					EventsEntityNode node = createRootNode(ea);
					node.setIgnore(getIgnore());
					rootNodes.add(node);
				}
			}
		}
		return rootNodes;
	}

	public void setMultiplo(boolean isMultiplo) {
		this.isMultiplo = isMultiplo;
	}

	public boolean isMultiplo() {
		return isMultiplo;
	}
	
}
