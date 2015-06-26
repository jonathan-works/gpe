package br.com.infox.epp.fluxo.xpdl.transition;

import java.io.Serializable;
import java.util.List;

import org.jbpm.graph.def.Transition;
import org.jdom2.Element;

import com.google.common.base.Strings;

import br.com.infox.epp.fluxo.xpdl.FluxoXPDL;
import br.com.infox.epp.fluxo.xpdl.IllegalXPDLException;
import br.com.infox.epp.fluxo.xpdl.XmlUtil;
import br.com.infox.epp.fluxo.xpdl.activities.ActivityXPDL;

public final class TransitionXPDL implements Serializable {

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String to;
    private String from;
    private Transition transition;

    private TransitionXPDL(String id, String name, String to, String from) throws IllegalTransitionXPDLException {
        this.id = id;
        this.name = name;
        this.to = to;
        this.from = from;
    }

    public static TransitionXPDL createInstance(Element element, String name, List<ActivityXPDL> activities) throws IllegalXPDLException {
        String id = XmlUtil.getAttributeValue(element, "Id");

        String elementName = XmlUtil.getAttributeValue(element, "Name");
        if (elementName == null || elementName.isEmpty()) {
            elementName = name;
        }

        String from = XmlUtil.getAttributeValue(element, "From");
        String to = XmlUtil.getAttributeValue(element, "To");
		if (from == null || from.isEmpty()) {
        	StringBuilder sb = new StringBuilder("Transição (");
        	sb.append(elementName);
        	sb.append(") ilegal. Nó de origem nulo");
        	if (!Strings.isNullOrEmpty(to)) {
        		ActivityXPDL nodeTo = findNodeById(activities, to);
        		if (nodeTo != null) {
	        		sb.append(" (nó de destino: ");
	        		sb.append(nodeTo.getName());
	        		sb.append(")");
        		}
        	}
            throw new IllegalTransitionXPDLException(sb.toString());
        }
        if (to == null || to.isEmpty()) {
        	StringBuilder sb = new StringBuilder("Transição (");
        	sb.append(elementName);
        	sb.append(") ilegal. Nó de destino nulo");
        	if (!Strings.isNullOrEmpty(from)) {
        		ActivityXPDL nodeFrom = findNodeById(activities, from);
        		if (from != null) {
	        		sb.append(" (nó de origem: ");
	        		sb.append(nodeFrom.getName());
	        		sb.append(")");
        		}
        	}
            throw new IllegalTransitionXPDLException(sb.toString());
        }
        if (to.equals(from)) {
        	StringBuilder sb = new StringBuilder("Transição (");
        	sb.append(elementName);
        	sb.append(") ilegal. Transição cíclica para o mesmo nó (");
    		ActivityXPDL nodeTo = findNodeById(activities, to);
    		if (nodeTo != null) {
        		sb.append(" (nó de destino: ");
        		sb.append(nodeTo.getName());
        		sb.append(")");
    		}
            throw new IllegalTransitionXPDLException(sb.toString());
        }

        return new TransitionXPDL(id, elementName, to, from);
    }

    public void setActivities(List<ActivityXPDL> activities) {
        ActivityXPDL fromNode = findNodeById(activities, from);
        ActivityXPDL toNode = findNodeById(activities, to);
        fromNode.setTransitionTo(toNode);
        transition = new Transition();
        if (getName().startsWith(FluxoXPDL.NO_NAME)) {
            transition.setName(toNode.getName());
        } else {
            transition.setName(getName());
        }
        fromNode.toNode().addLeavingTransition(transition);
        transition.setFrom(fromNode.toNode());

        toNode.toNode().addArrivingTransition(transition);
        transition.setTo(toNode.toNode());
    }

    public Transition toTransition() {
        return transition;
    }

    public static ActivityXPDL findNodeById(List<ActivityXPDL> list, String id) {
        boolean find = false;
        ActivityXPDL temp = null;
        int i = 0;
        while (!find && i < list.size()) {
            temp = list.get(i++);
            if (temp.getId().equals(id)) {
                find = true;
            }
        }
        return temp;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    @Override
    public String toString() {
        String newLine = System.getProperty("line.separator");
        String string = "[TransictionXPDL] name: " + name;
        if (transition != null) {
            string += newLine + "[Trasintion] from: " + transition.getFrom()
                    + newLine + " to: " + transition.getTo();
        }
        return string;
    }

}
