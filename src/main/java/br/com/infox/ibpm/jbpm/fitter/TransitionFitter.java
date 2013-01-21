package br.com.infox.ibpm.jbpm.fitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;

import br.com.infox.ibpm.jbpm.ProcessBuilder;
import br.com.infox.ibpm.jbpm.handler.TransitionHandler;
import br.com.itx.util.ComponentUtil;

@Name(TransitionFitter.NAME)
@Scope(ScopeType.CONVERSATION)
public class TransitionFitter implements Serializable, Fitter {
	

	private static final long serialVersionUID = 1L;

	public static final String NAME = "transitionFitter";
	
	private List<SelectItem> transitionsItems;
	private String newNodeTransitionName;
	private TransitionHandler newNodeTransition;
	private Transition currentTransition = new Transition();
	private List<TransitionHandler> arrivingTransitions;
	private List<TransitionHandler> leavingTransitions;
	private List<TransitionHandler> transitionList;
	private List<String[]> transitionNames;
	
	private ProcessBuilder pb = ComponentUtil.getComponent(ProcessBuilder.NAME);

	public void changeTransition(TransitionHandler th, String type) {
		Node oldNodeTransition = pb.getOldNodeTransition();
		Transition t = th.getTransition();
		if (type.equals("from")) {
			if (t.getFrom() != null) {
				t.getFrom().addLeavingTransition(t);
			}
			if (oldNodeTransition != null) {
				oldNodeTransition.removeLeavingTransition(t);
			}
		} else {
			Node to = t.getTo();
			if (to != null) {
				to.addArrivingTransition(t);
			}
			if (oldNodeTransition != null) {
				oldNodeTransition.removeArrivingTransition(t);
			}
			t.setTo(to);
		}
		if (t.getName() == null || t.getName().equals("")) {
			try {
				t.setName(t.getTo().getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	//TODO modificar para visibilidade privada assim que possível
	public void checkTransitions() {
		List<Node> nodes = pb.getNodes();
		clear();
		Map<Node, String> nodeMessageMap = new HashMap<Node, String>();
		for (Node n : nodes) {
			if (!(n instanceof EndState)) {
				List<Transition> transitions = n.getLeavingTransitions();
				if (transitions == null || transitions.isEmpty()) {
					nodeMessageMap.put(n, "Nó sem transição de saída");
				}
			}
			if (!(n instanceof StartState)) {
				Set<Transition> transitionSet = n.getArrivingTransitions();
				if (transitionSet == null || transitionSet.isEmpty()) {
					nodeMessageMap.put(n, "Nó sem transição de entrada");
				}
			}
		}
		pb.setNodeMessageMap(nodeMessageMap);
	}
	
	public void addTransition(String type) {
		Node currentNode = pb.getCurrentNode();
		Transition t = new Transition("");
		if (type.equals("from")) {
			currentNode.addArrivingTransition(t);
			if (arrivingTransitions == null) {
				arrivingTransitions = new ArrayList<TransitionHandler>();
			}
			arrivingTransitions.add(new TransitionHandler(t));
		} else if (type.equals("to")) {
			currentNode.addLeavingTransition(t);
			if (leavingTransitions == null) {
				leavingTransitions = new ArrayList<TransitionHandler>();
			}
			leavingTransitions.add(new TransitionHandler(t));
		}
		checkTransitions();
	}

	public void removeTransition(TransitionHandler th, String type) {
		Node currentNode = pb.getCurrentNode();
		Transition t = th.getTransition();
		if (type.equals("from") && t.getFrom() != null) {
			t.getFrom().removeLeavingTransition(t);
		} else if (type.equals("to") && t.getTo() != null) {
			t.getTo().removeArrivingTransition(t);
		}
		clearArrivingAndLeavingTransitions();
		currentNode.removeArrivingTransition(t);
		currentNode.removeLeavingTransition(t);
		checkTransitions();
	}
	
	public void setCurrentTransition(Transition currentTransition) {
		this.currentTransition = currentTransition;
	}

	public Transition getCurrentTransition() {
		return currentTransition;
	}
	
	public String getNewNodeTransitionName() {
		return newNodeTransitionName;
	}

	public void setNewNodeTransitionName(String newNodeTransitionName) {
		this.newNodeTransitionName = newNodeTransitionName;
		setNewNodeTransition(newNodeTransitionName);
	}

	public void setNewNodeTransition(String newNodeTransition) {
		if (transitionList == null) {
			getTransitions();
		}
		this.newNodeTransition = TransitionHandler.asObject(newNodeTransition,
				transitionList);
	}

	public TransitionHandler getNewNodeTransition() {
		return newNodeTransition;
	}

	public List<TransitionHandler> getArrivingTransitions() {
		Node currentNode = pb.getCurrentNode();
		if (arrivingTransitions == null) {
			if (currentNode != null
					&& currentNode.getArrivingTransitions() != null) {
				arrivingTransitions = TransitionHandler.getList(currentNode
						.getArrivingTransitions());
			}
		}
		return arrivingTransitions;
	}

	public List<TransitionHandler> getLeavingTransitions() {
		Node currentNode = pb.getCurrentNode();
		if (leavingTransitions == null) {
			if (currentNode != null
					&& currentNode.getLeavingTransitions() != null) {
				leavingTransitions = TransitionHandler.getList(currentNode
						.getLeavingTransitions());
			}
		}
		return leavingTransitions;
	}
	
	public List<TransitionHandler> getTransitions() {
		List<Node> nodes = pb.getNodes();
		if (transitionList == null) {
			transitionList = new ArrayList<TransitionHandler>();
			for (Node n : nodes) {
				if (n.getLeavingTransitions() != null) {
					transitionList.addAll(TransitionHandler.getList(n
							.getLeavingTransitions()));
				}
			}
		}
		return transitionList;
	}
	
	/**
	 * Seta a #{true} na condição da transição para o botão não ser exibido na
	 * tab de saída do fluxo.
	 * 
	 * @param th
	 */
	public void setTransitionButton(TransitionHandler th) {
		if (th.getTransition().getCondition() == null) {
			th.getTransition().setCondition("#{true}");
		} else {
			th.getTransition().setCondition(null);
		}
	}
	
	public List<String[]> getTransitionNames() {
		if (transitionNames == null) {
			getTransitions();
			transitionNames = new ArrayList<String[]>();
			for (TransitionHandler th : transitionList) {
				String[] names = { th.getFromName(), th.getToName() };
				transitionNames.add(names);
			}
		}
		return transitionNames;
	}
	
	
	public List<SelectItem> getTransitionsItems(List<Node> nodes) {
		if (transitionsItems == null) {
			transitionsItems = new ArrayList<SelectItem>();
			transitionsItems.add(new SelectItem(null, "[Selecione...]"));
			for (Node n : nodes) {
				if (n.getLeavingTransitions() != null) {
					for (TransitionHandler t : TransitionHandler.getList(n
							.getLeavingTransitions())) {
						transitionsItems.add(new SelectItem(t));
					}
				}
			}
		}
		return transitionsItems;
	}
	
	public void setTransitionsItems(List<SelectItem> transitionsItems) {
		this.transitionsItems = transitionsItems;
	}
	
	@Override
	public void clear(){
		transitionList = null;
		transitionsItems = null;
	}
	
	public void clearNewNodeTransition(){
		newNodeTransition = null;
	}
	
	public void clearArrivingAndLeavingTransitions(){
		arrivingTransitions = null;
		leavingTransitions = null;
	}
}
