package br.com.infox.ibpm.jbpm.fitter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.SelectItem;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;

import br.com.infox.ibpm.jbpm.handler.TransitionHandler;

@Name(TransitionFitter.NAME)
@AutoCreate
public class TransitionFitter extends Fitter implements Serializable {
	

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(TransitionFitter.class);

	public static final String NAME = "transitionFitter";
	
	private List<SelectItem> transitionsItems;
	private String newNodeTransitionName;
	private TransitionHandler newNodeTransition;
	private Transition currentTransition = new Transition();
	private List<TransitionHandler> arrivingTransitions;
	private List<TransitionHandler> leavingTransitions;
	private List<TransitionHandler> transitionList;
	private List<String[]> transitionNames;
	
	public void changeTransition(TransitionHandler th, String type) {
		Node oldNodeTransition = getProcessBuilder().getNodeFitter().getOldNodeTransition();
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
				LOG.error("changeTransition()", e);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void checkTransitions() {
		List<Node> nodes = getProcessBuilder().getNodeFitter().getNodes();
		clear();
		Map<Node, String> nodeMessageMap = new HashMap<Node, String>();
		for (Node n : nodes) {
			if (!(n instanceof EndState)) {
				List<Transition> transitions = n.getLeavingTransitions();
				if (transitions == null || transitions.isEmpty()) {
					nodeMessageMap.put(n, "N� sem transi��o de sa�da");
				}
			}
			if (!(n instanceof StartState)) {
				Set<Transition> transitionSet = n.getArrivingTransitions();
				if (transitionSet == null || transitionSet.isEmpty()) {
					nodeMessageMap.put(n, "N� sem transi��o de entrada");
				}
			}
		}
		getProcessBuilder().getNodeFitter().setNodeMessageMap(nodeMessageMap);
	}
	
	public void addTransition(String type) {
		Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
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
		Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
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

	@SuppressWarnings("unchecked")
	public List<TransitionHandler> getArrivingTransitions() {
		Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
		if (arrivingTransitions == null && currentNode != null && currentNode.getArrivingTransitions() != null) {
			arrivingTransitions = TransitionHandler.getList(currentNode.getArrivingTransitions());
		}
		return arrivingTransitions;
	}

	@SuppressWarnings("unchecked")
	public List<TransitionHandler> getLeavingTransitions() {
		Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
		if (leavingTransitions == null && currentNode != null && currentNode.getLeavingTransitions() != null) {
			leavingTransitions = TransitionHandler.getList(currentNode.getLeavingTransitions());
		}
		return leavingTransitions;
	}
	
	@SuppressWarnings("unchecked")
	public List<TransitionHandler> getTransitions() {
		List<Node> nodes = getProcessBuilder().getNodeFitter().getNodes();
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
	 * Seta a #{true} na condi��o da transi��o para o bot�o n�o ser exibido na
	 * tab de sa�da do fluxo.
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
	
	
	@SuppressWarnings("unchecked")
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
