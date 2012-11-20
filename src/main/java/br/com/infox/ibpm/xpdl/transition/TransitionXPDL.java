package br.com.infox.ibpm.xpdl.transition;

import java.io.Serializable;
import java.util.List;

import org.jbpm.graph.def.Transition;
import org.jdom.Element;

import br.com.infox.ibpm.xpdl.FluxoXPDL;
import br.com.infox.ibpm.xpdl.IllegalXPDLException;
import br.com.infox.ibpm.xpdl.activities.ActivityXPDL;
import br.com.itx.util.XmlUtil;


public class TransitionXPDL implements Serializable {
	
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
	
	public static TransitionXPDL createInstance(Element element, String name) throws IllegalXPDLException {
		String id = XmlUtil.getAttributeValue(element, "Id");
		
		String elementName = XmlUtil.getAttributeValue(element, "Name");
		if(elementName == null || elementName.isEmpty()) {
			elementName = name;
		}
		
		String from = XmlUtil.getAttributeValue(element, "From");
		if(from == null || from.isEmpty()) {
			throw new IllegalTransitionXPDLException("Transição ilegal. Nó de destino nulo");
		}
		
		String to = XmlUtil.getAttributeValue(element, "To");
		if(to == null || to.isEmpty()) {
			throw new IllegalTransitionXPDLException("Transição ilegal. Nó de origem nulo");
		}
		if(to.equals(from)) {
			throw new IllegalTransitionXPDLException("Transição ilegal. Transição cíclica para o mesmo nó.");
		}
		
		return new TransitionXPDL(id, elementName, to, from);
	}
	
	public void setActivities(List<ActivityXPDL> activities) {
		ActivityXPDL fromNode = findNodeById(activities, from);
		ActivityXPDL toNode = findNodeById(activities, to);
		fromNode.setTransitionTo(toNode);
		transition = new Transition();
		if(getName().startsWith(FluxoXPDL.NO_NAME)) {
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
	
	public ActivityXPDL findNodeById(List<ActivityXPDL> list, String id) {
		boolean find = false;
		ActivityXPDL temp = null;
		int i = 0;
		while(!find && i < list.size()) {
			temp = list.get(i++);
			if(temp.getId().equals(id)) {
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
		if(transition != null) {
			string += newLine + "[Trasintion] from: " + transition.getFrom() + newLine + " to: " + transition.getTo();
		}
		return string;
	}

}
