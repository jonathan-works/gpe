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
		int tabIndex = 0;
		for (UIComponent child : tabPanel.getChildren()) {
			if (!(child instanceof Tab)) {
				break;
			}
			Tab tab = (Tab) child;
			writer.startElement("li", null);
			writer.writeAttribute("name", tab.getName(), "name");
			writer.startElement("a", null);
			writer.writeAttribute("href", "#" + tab.getId(), "id");
			writer.writeText(tab.getTitle(), "title");
			writer.endElement("a");
			writer.endElement("li");
			
			tabPanel.getTabIndexMap().put(tab.getName(), tabIndex++);
			if (tabPanel.getSwitchType().equals("ajax")) {
				String activeTab = tabPanel.getActiveTab();
				if ((activeTab == null && tabPanel.getTabIndexMap().get(tab.getName()) == 0) || 
						tab.getName().equals(tabPanel.getActiveTab())) {
					tab.setRendered(true);
				} else {
					tab.setRendered(false);
				}
			}
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
		writer.writeText(createTabInitializationJavascript(tabPanel, context), null);
		writer.endElement("script");
	}
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		super.decode(context, component);
		TabPanel tabPanel = (TabPanel) component;
		String newTab = context.getExternalContext().getRequestParameterMap().get("newTab");
		if (newTab != null) {
			tabPanel.setActiveTab(newTab);
		}
	}

	private String createTabInitializationJavascript(TabPanel tabPanel, FacesContext context) {
		StringBuffer sb = new StringBuffer();
		sb.append("$(function() {");
		sb.append("$('#");
		sb.append(tabPanel.getId());
		sb.append("').tabs({");
		sb.append("active: ");
		String activeTab = tabPanel.getActiveTab();
		if (activeTab != null) {
			sb.append(tabPanel.getTabIndexMap().get(activeTab));
		} else {
			sb.append(0);
		}
		sb.append(",");
		sb.append("beforeActivate: ");
		sb.append(createBeforeActivateJavascript(tabPanel));
		sb.append("});");
		sb.append("});");
		return sb.toString();
	}

	private String createBeforeActivateJavascript(TabPanel tabPanel) {
		StringBuffer sb = new StringBuffer("function(event, ui) {");
		if (tabPanel.getSwitchType().equals("client")) {
			sb.append("}");
			return sb.toString();
		}
		sb.append("event.preventDefault();");
		sb.append("jsf.ajax.request(this, event, {");
		sb.append("render: '");
		sb.append(tabPanel.getId());
		sb.append("',");
		sb.append("newTab: ");
		sb.append("ui.newTab.attr('name')");
		sb.append("});");
		sb.append("}");
		return sb.toString();
	}
}
