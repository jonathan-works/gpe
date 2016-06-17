package br.com.infox.ibpm.process.definition.fitter;

import static br.com.infox.core.comparators.Comparators.bySelectItemLabelAsc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.node.EndState;
import org.jbpm.graph.node.StartState;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.processo.timer.TaskExpiration;
import br.com.infox.epp.processo.timer.manager.TaskExpirationManager;
import br.com.infox.ibpm.transition.TransitionHandler;
import br.com.infox.ibpm.util.BpmUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Named
@ViewScoped
public class TransitionFitter extends Fitter implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(TransitionFitter.class);

    @Inject
    private TaskExpirationManager taskExpirationManager;
    @Inject
    private InfoxMessages infoxMessages;

    private List<SelectItem> transitionsItems;
    private String newNodeTransitionName;
    private TransitionHandler newNodeTransition;
    private Transition currentTransition = new Transition();
    private List<TransitionHandler> arrivingTransitions;
    private List<TransitionHandler> leavingTransitions;
    private List<TransitionHandler> transitionList;
    private List<String[]> transitionNames;

    public void checkTransitions() {
        List<Node> nodes = getProcessBuilder().getNodeFitter().getNodes();
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
                Collection<Transition> transitionSet = n.getArrivingTransitions();
                if (transitionSet == null || transitionSet.isEmpty()) {
                    nodeMessageMap.put(n, "Nó sem transição de entrada");
                }
            }
        }
        getProcessBuilder().getNodeFitter().setNodeMessageMap(nodeMessageMap);
    }

    public void addTransition(String type) {
        Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
        Transition t = new Transition("");
        t.setKey(BpmUtil.generateKey());
        if ("from".equals(type)) {
            currentNode.addArrivingTransition(t);
            if (arrivingTransitions == null) {
                arrivingTransitions = new ArrayList<TransitionHandler>();
            }
            arrivingTransitions.add(new TransitionHandler(t));
        } else if ("to".equals(type)) {
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
        Transition transition = th.getTransition();
        if ("from".equals(type) && transition.getFrom() != null) {
            transition.getFrom().removeLeavingTransition(transition);
        } else if ("to".equals(type) && transition.getTo() != null) {
            transition.getTo().removeArrivingTransition(transition);
        }
        clearArrivingAndLeavingTransitions();
        currentNode.removeArrivingTransition(transition);
        currentNode.removeLeavingTransition(transition);
        removeTaskExpiration(currentNode.getName(), th.getName());
        getProcessBuilder().getNodeFitter().removeListener(currentNode, transition);
        checkTransitions();
    }

    public TransitionHandler connectNodes(Node from, Node to) {
        Transition transition = new Transition(to.getName());
        transition.setFrom(from);
        transition.setTo(to);
        from.addLeavingTransition(transition);
        to.addArrivingTransition(transition);
        clearArrivingAndLeavingTransitions();
        return new TransitionHandler(transition);
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
        this.newNodeTransition = TransitionHandler.asObject(newNodeTransition, transitionList);
    }

    public TransitionHandler getNewNodeTransition() {
        return newNodeTransition;
    }

    public List<TransitionHandler> getArrivingTransitions() {
        Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
        if (arrivingTransitions == null && currentNode != null
                && currentNode.getArrivingTransitions() != null) {
            arrivingTransitions = TransitionHandler.getList(currentNode.getArrivingTransitions());
        }
        return arrivingTransitions;
    }

    public List<TransitionHandler> getLeavingTransitions() {
        Node currentNode = getProcessBuilder().getNodeFitter().getCurrentNode();
        if (leavingTransitions == null && currentNode != null
                && currentNode.getLeavingTransitions() != null) {
            leavingTransitions = TransitionHandler.getList(currentNode.getLeavingTransitions());
        }
        return leavingTransitions;
    }

    public List<TransitionHandler> getTransitions() {
        List<Node> nodes = getProcessBuilder().getNodeFitter().getNodes();
        if (transitionList == null) {
            transitionList = new ArrayList<TransitionHandler>();
            for (Node n : nodes) {
                if (n.getLeavingTransitions() != null) {
                    transitionList.addAll(TransitionHandler.getList(n.getLeavingTransitions()));
                }
            }
        }
        return transitionList;
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
            for (Node n : nodes) {
                if (n.getLeavingTransitions() != null) {
                    for (TransitionHandler t : TransitionHandler.getList((List<Transition>) n.getLeavingTransitions())) {
                        transitionsItems.add(new SelectItem(t));
                    }
                }
            }
            Collections.sort(transitionsItems, bySelectItemLabelAsc());
            transitionsItems.add(0,new SelectItem(null, infoxMessages.get("process.transition.select")));
        }
        return transitionsItems;
    }

    public void setTransitionsItems(List<SelectItem> transitionsItems) {
        this.transitionsItems = transitionsItems;
    }

    @Override
    public void clear() {
        transitionList = null;
        transitionsItems = null;
    }

    public void clearNewNodeTransition() {
        newNodeTransition = null;
    }

    public void clearArrivingAndLeavingTransitions() {
        arrivingTransitions = null;
        leavingTransitions = null;
    }
    
    private void removeTaskExpiration(String taskName, String transition) {
        Fluxo fluxo = getProcessBuilder().getFluxo();
        try {
            TaskExpiration te = taskExpirationManager.getByFluxoAndTaskName(fluxo, taskName);
            if (te != null && te.getTransition().equals(transition)) {
                taskExpirationManager.remove(te);
            }
        } catch (DAOException e) {
            LOG.error("transitionFitter.removeTaskExpiration()", e);
        }
    }
    
}
