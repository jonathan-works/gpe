package br.com.infox.ibpm.jbpm.xpdl.element;

import java.io.Serializable;

import org.jdom.Element;

import br.com.itx.util.XmlUtil;

public class ElementXPDL implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Element element;
	private String id;
	private String name;
	private GraphicsInfoXPDL graphics;
	
	public ElementXPDL(Element element2, String name) {
		element = element2;
		graphics = new GraphicsInfoXPDL(XmlUtil.getChildByIndex(element, "NodeGraphicsInfos", 0));
		this.name = element2.getAttributeValue("Name");
		if(this.name == null || this.name.isEmpty())
			this.name = name;
		id = element2.getAttributeValue("Id");
	}
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public GraphicsInfoXPDL getGraphics() {
		return graphics;
	}
	public Element getElement() {
		return element;
	}
	public void setElement(Element element) {
		this.element = element;
	}

	@Override
	public String toString() {
		return "[ElementXPDL] name: " + name + graphics.toString();
	}
}
