package br.com.infox.component.tabs;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import br.com.infox.component.HtmlConstants;

/**
 * Renderer padrão do componente TabPanel.
 * @author gabriel
 *
 */
@FacesRenderer(componentFamily = TabPanelRenderer.COMPONENT_FAMILY, rendererType = TabPanelRenderer.RENDERER_TYPE)
public class TabPanelRenderer extends Renderer {
	public static final String RENDERER_TYPE = "br.com.infox.component.tabs.TabPanelRenderer";
	public static final String COMPONENT_FAMILY = "br.com.infox.component.tabs";
	
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		super.encodeBegin(context, component);
		TabPanel tabPanel = (TabPanel) component;
		ResponseWriter writer = context.getResponseWriter();
		tabPanel.setTabIndexMap(null);
		writer.startElement(HtmlConstants.DIV_ELEMENT, tabPanel);
		writer.writeAttribute(HtmlConstants.ID_ATTR, tabPanel.getClientId(context), "clientId");
		if (tabPanel.getStyle() != null) {
			writer.writeAttribute(HtmlConstants.STYLE_ATTR, tabPanel.getStyle(), "style");
		}
		if (tabPanel.getStyleClass() != null) {
			writer.writeAttribute(HtmlConstants.CLASS_ATTR, tabPanel.getStyleClass(), "styleClass");
		}
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		super.encodeEnd(context, component);
		TabPanel tabPanel = (TabPanel) component;
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement(HtmlConstants.DIV_ELEMENT);
		writer.startElement(HtmlConstants.SCRIPT_ELEMENT, null);
		writer.writeText(createTabInitializationJavascript(tabPanel, context), null);
		writer.endElement(HtmlConstants.SCRIPT_ELEMENT);
	}
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		super.decode(context, component);
		TabPanel tabPanel = (TabPanel) component;
		
		String source = context.getExternalContext().getRequestParameterMap().get("javax.faces.source");
		if (source == null || !source.equals(tabPanel.getClientId(context))) {
			return;
		}
		
		String newTab = context.getExternalContext().getRequestParameterMap().get("newTab");
		if (newTab != null) {
			tabPanel.setActiveTab(newTab);
		}
	}

	private String createTabInitializationJavascript(TabPanel tabPanel, FacesContext context) {
		StringBuffer sb = new StringBuffer();
		sb.append("$(function() {");
		sb.append("$('#");
		sb.append(tabPanel.getClientId(context).replace(":", "\\\\:"));
		sb.append("').tabs({");
		sb.append("active: ");
		sb.append(tabPanel.getTabIndexMap().get(tabPanel.getActiveTab()));
		sb.append(",");
		sb.append("disabled: [");
		boolean ok = false;
		for (Tab tab : tabPanel.getTabs()) {
			if (tab.isDisabled()) {
				sb.append(tabPanel.getTabIndexMap().get(tab.getName()));
				sb.append(",");
				ok = true;
			}
		}
		if (ok) {
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append("], ");
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
