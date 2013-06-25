package br.com.infox.component.tabs;

import java.io.IOException;
import java.util.Iterator;

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
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		super.encodeBegin(context, component);
		TabPanel tabPanel = (TabPanel) component;
		ResponseWriter writer = context.getResponseWriter();
		tabPanel.setTabIndexMap(null);
		writer.startElement("div", tabPanel);
		writer.writeAttribute("id", tabPanel.getClientId(), "clientId");
	}
	
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		if (context == null || component == null) {
            throw new NullPointerException();
        }
        if (component.getChildCount() > 0) {
        	TabPanel panel = (TabPanel) component;
        	Iterator<UIComponent> kids = component.getChildren().iterator();
        	while (kids.hasNext()) {
        	    UIComponent kid = kids.next();
        	    if (kid instanceof Tab) {
        	    	Tab tab = (Tab) kid;
        	    	if (tab.getName().equals(panel.getActiveTab())) {
        	    		tab.encodeAll(context);
        	    	}
        	    } else {
        	    	kid.encodeAll(context);
        	    }
        	}
        }
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
		sb.append(tabPanel.getClientId().replace(":", "\\\\:"));
		sb.append("').tabs({");
		sb.append("active: ");
		sb.append(tabPanel.getTabIndexMap().get(tabPanel.getActiveTab()));
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
		sb.append("__");
		sb.append(tabPanel.getClientId().replace(":", ""));
		sb.append("(event, ui);");
		sb.append("}");
		return sb.toString();
	}
}
