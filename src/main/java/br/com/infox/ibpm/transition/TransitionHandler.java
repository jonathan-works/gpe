package br.com.infox.ibpm.transition;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.core.Events;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Node.NodeType;
import org.jbpm.graph.def.Transition;

import br.com.infox.ibpm.node.converter.NodeConverter;
import br.com.infox.ibpm.process.definition.ProcessBuilder;
import br.com.infox.ibpm.process.definition.fitter.NodeFitter;

public class TransitionHandler implements Serializable {

    private static final long serialVersionUID = 4373236937521654740L;
    
    public static final String OCCULT_TRANSITION = "OCCULT_TRANSITION";
    public static final String EVENT_JBPM_TRANSITION_NAME_CHANGED = "jbpmTransitionNameChanged";

    private Transition transition;

    /**
     * Usado para definir se a transição será visível na saída do nó para o
     * usuário.
     */
    private boolean occultTransitionButton;

    public TransitionHandler(Transition transition) {
        this.transition = transition;
        this.setOccultTransitionButton(transition.getDescription() != null && OCCULT_TRANSITION.contains(transition.getDescription()));
    }

    public String getName() {
        return transition.getName();
    }

    public void setName(String name) {
        name = name.trim();
        if (name != null && !name.equals(transition.getName())) {
            Events.instance().raiseEvent(EVENT_JBPM_TRANSITION_NAME_CHANGED,
                    ProcessBuilder.instance().getFluxo(), transition.getFrom().getName(),
                    transition.getName(), name);
            transition.setName(name);
        }
    }

    public Transition getTransition() {
        return transition;
    }

    public void setFrom(String from) {
        transition.setFrom(NodeConverter.getAsObject(from));
    }

    public String getFrom() {
        return transition.getFrom() == null ? null : transition.getFrom().toString();
    }

    public String getFromName() {
        return transition.getFrom() == null ? null : transition.getFrom().getName();
    }

    public void setTo(String to) {
        transition.setTo(NodeConverter.getAsObject(to));
    }

    public String getTo() {
        return transition.getTo() == null ? null : transition.getTo().toString();
    }

    public String getToName() {
        return transition.getTo() == null ? null : transition.getTo().getName();
    }

    public static List<TransitionHandler> getList(
            Collection<Transition> transitions) {
        List<TransitionHandler> list = new ArrayList<TransitionHandler>();
        for (Transition t : transitions) {
            list.add(new TransitionHandler(t));
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName()).append(" [");
        if (getFrom() != null) {
            sb.append(getFromName());
        }
        sb.append(" -> ");
        if (getTo() != null) {
            sb.append(getToName());
        }
        sb.append("]");
        return sb.toString();
    }

    public static String asString(TransitionHandler th) {
        if (th == null) {
            return null;
        }
        return th.getFromName() + " -> " + th.getToName();
    }

    public static TransitionHandler asObject(String name,
            List<TransitionHandler> transitions) {
        if (name == null) {
            return null;
        }
        for (TransitionHandler t : transitions) {
            if (t.toString().equals(name)) {
                return t;
            }
        }
        return null;
    }

    @SuppressWarnings(UNCHECKED)
    public boolean isInDecisionNode() {
        NodeFitter nodeFitter = (NodeFitter) Component.getInstance(NodeFitter.NAME);
        Node currentNode = nodeFitter.getCurrentNode();
        if (currentNode != null
                && currentNode.getNodeType().equals(NodeType.Decision)) {
            return isInNode(currentNode.getLeavingTransitions());
        }
        return false;
    }

    @SuppressWarnings(UNCHECKED)
    public boolean isInForkNode() {
        NodeFitter nodeFitter = (NodeFitter) Component.getInstance(NodeFitter.NAME);
        Node currentNode = nodeFitter.getCurrentNode();
        if (currentNode != null
                && currentNode.getNodeType().equals(NodeType.Fork)) {
            return isInNode(currentNode.getLeavingTransitions());
        }
        return false;
    }

    @SuppressWarnings(UNCHECKED)
    public boolean isInJoinNode() {
        NodeFitter nodeFitter = (NodeFitter) Component.getInstance(NodeFitter.NAME);
        Node currentNode = nodeFitter.getCurrentNode();
        if (currentNode != null
                && currentNode.getNodeType().equals(NodeType.Join)) {
            return isInNode(currentNode.getArrivingTransitions());
        }
        return false;
    }

    public boolean canDefineCondition() {
        return isInForkNode() || isInJoinNode();
    }

    private boolean isInNode(Collection<Transition> transitions) {
        return transitions != null && transitions.contains(this.transition);
    }

    public boolean isOccultTransitionButton() {
        return occultTransitionButton;
    }

    public void setOccultTransitionButton(boolean occultTransitionButton) {
        this.occultTransitionButton = occultTransitionButton;
    }
}
