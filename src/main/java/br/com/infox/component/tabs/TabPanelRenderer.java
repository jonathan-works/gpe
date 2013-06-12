package br.com.infox.component.tabs;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

@FacesRenderer(componentFamily = TabPanelRenderer.COMPONENT_FAMILY, rendererType = TabPanelRenderer.RENDERER_TYPE)
public class TabPanelRenderer extends Renderer {
	public static final String RENDERER_TYPE = "br.com.infox.component.tabs.TabPanelRenderer";
	public static final String COMPONENT_FAMILY = "br.com.infox.component.tabs";
	
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		super.encodeBegin(context, component);
		TabPanel tabPanel = (TabPanel) component;
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", tabPanel);
		writer.writeAttribute("id", tabPanel.getId(), "id");
		writer.startElement("ul", null);
		for (UIComponent child : tabPanel.getChildren()) {
			if (!(child instanceof Tab)) {
				break;
			}
			Tab tab = (Tab) child;
			writer.startElement("li", null);
			writer.startElement("a", null);
			writer.writeAttribute("href", "#" + tab.getId(), "id");
			writer.writeText(tab.getTitle(), "title");
			writer.endElement("a");
			writer.endElement("li");
		}
		writer.endElement("ul");
	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		super.encodeEnd(context, component);
		TabPanel tabPanel = (TabPanel) component;
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
		writer.startElement("script", null);
		StringBuffer sb = new StringBuffer();
		sb.append("$(function() {");
		sb.append("$('#");
		sb.append(tabPanel.getId());
		sb.append("').tabs();");
		sb.append("});");
		writer.writeText(sb.toString(), null);
		writer.endElement("script");
	}
}
