package br.com.infox.ibpm.xpdl.element;

import java.io.Serializable;

import org.jdom.Element;

import br.com.itx.util.XmlUtil;

public class ElementXPDL implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Element element;
	private String id;
	private String name;
	private GraphicsInfoXPDL graphics;
	
	protected ElementXPDL(Element element, String name) {
		this.element = element;
		this.id = this.element.getAttributeValue("Id");
		this.name = this.element.getAttributeValue("Name");
		if(this.name == null || this.name.isEmpty())
			this.name = name;
		this.graphics = GraphicsInfoXPDL.createInstance(XmlUtil.getChildByIndex(this.element, "NodeGraphicsInfos", 0));
	}
	
	public static ElementXPDL createInstance(Element element, String name) {
		return new ElementXPDL(element, name);
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
