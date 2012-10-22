package br.com.infox.ibpm.jbpm.xpdl.transition;

import java.util.List;

import org.jbpm.graph.def.Transition;
import org.jdom.Element;

import br.com.infox.ibpm.jbpm.xpdl.FluxoXPDL;
import br.com.infox.ibpm.jbpm.xpdl.activities.ActivityXPDL;
import br.com.itx.util.XmlUtil;


public class TransitionXPDL {
	
	private Element element;
	private String id;
	private String name;
	private String to;
	private String from;
	private Transition transition;
	
	public TransitionXPDL(Element element, String name) throws IllegalTransitionXPDLException {
		this.element = element;
		this.name = XmlUtil.getAttributeValue(element, "Name");
		id = XmlUtil.getAttributeValue(element, "Id");
		from = XmlUtil.getAttributeValue(element, "From");
		to = XmlUtil.getAttributeValue(element, "To");
		checkAttributes(name);
	}
	
	private void checkAttributes(String name) throws IllegalTransitionXPDLException {
		if(this.name == null || this.name.isEmpty())
			this.name = name;
		if(from == null || from.isEmpty())
			throw new IllegalTransitionXPDLException("Transição ilegal. Nó de destino nulo");
		if(to == null || to.isEmpty())
			throw new IllegalTransitionXPDLException("Transição ilegal. Nó de origem nulo");
		if(to.equals(from))
			throw new IllegalTransitionXPDLException("Transição ilegal. Transição cíclica para o mesmo nó.");
	}
	
	public Transition toTransition(List<ActivityXPDL> list) {
		if(list == null) {
			return null;
		}
		if(transition == null) {
			ActivityXPDL fromNode = findNodeById(list, from);
			ActivityXPDL toNode = findNodeById(list, to);
			fromNode.setTransitionTo(toNode);
			transition = new Transition();
			if(getName().startsWith(FluxoXPDL.NO_NAME)) {
				transition.setName(toNode.getName());
			} else {
				transition.setName(getName());
			}
			transition.setFrom(fromNode.toNode());
			transition.setTo(toNode.toNode());
		}
		return transition;
	}
	
	public ActivityXPDL findNodeById(List<ActivityXPDL> list, String id) {
		boolean find = false;
		ActivityXPDL temp = null;
		int i = 0;
		while(!find && i < list.size()) {
			temp = list.get(i++);
			if(temp.getId().equals(id))
				find = true;
		}
		return temp;
	}

	public String getName() {
		return name;
	}
	
	public Element getElement() {
		return element;
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
		if(transition != null)
			string += newLine + "[Trasintion] from: " + transition.getFrom() + newLine + " to: " + transition.getTo();
		return string;
	}
}
