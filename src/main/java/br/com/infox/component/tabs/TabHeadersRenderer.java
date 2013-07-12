package br.com.infox.component.tabs;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import br.com.infox.component.HtmlConstants;

@FacesRenderer(componentFamily = TabHeadersRenderer.COMPONENT_FAMILY, rendererType = TabHeadersRenderer.RENDERER_TYPE)
public class TabHeadersRenderer extends Renderer {
	public static final String RENDERER_TYPE = "br.com.infox.component.tabs.TabHeadersRenderer";
	public static final String COMPONENT_FAMILY = "br.com.infox.component.tabs";
	
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		super.encodeBegin(context, component);
		ResponseWriter writer = context.getResponseWriter();
		TabHeaders tabHeaders = (TabHeaders) component;
		TabPanel panel = tabHeaders.getTabPanel();
		writer.startElement(HtmlConstants.UL_ELEMENT, tabHeaders);
		tabHeaders.setId(panel.getId() + "_header_container");
		writer.writeAttribute("id", tabHeaders.getClientId(context), null);
		writer.writeAttribute(HtmlConstants.CLASS_ATTR, getDefaultCssClassesHeaderContainer(), null);
		for (Tab tab : panel.getTabs()) {
			if (tab.isRendered()) {
				writer.startElement(HtmlConstants.LI_ELEMENT, null);
				writer.writeAttribute(HtmlConstants.NAME_ATTR, tab.getName(), "name");
				writer.writeAttribute("data-tab-id", tab.getClientId(context), null);
				
				encodeOnClick(context, writer, tab, tabHeaders);
				encodeBehaviors(context, writer, tab);
				encodeCssClasses(writer, tab);
				encodeHeaderLink(context, writer, tab);
				
				writer.endElement(HtmlConstants.LI_ELEMENT);
			}
		}
		writer.endElement(HtmlConstants.UL_ELEMENT);
	}

	private void encodeOnClick(FacesContext context, ResponseWriter writer, Tab tab, TabHeaders tabHeaders) throws IOException {
		if (tab.isDisabled()) {
			return;
		}
		
		StringBuilder sb = new StringBuilder("jsf.ajax.request(");
		sb.append("document.getElementById('");
		sb.append(tabHeaders.getClientId(context));
		sb.append("').getElementsByClassName('ui-state-active')[0].attributes['data-tab-id'].value");
		sb.append(", {'type': 'activateTab'}, {");
		sb.append("execute: '");
		sb.append(tab.getExecute());
		sb.append("', render: '");
		sb.append(tab.getRender());
		sb.append("', newTab: '");
		sb.append(tab.getName());
		sb.append("'});");

		writer.writeAttribute(HtmlConstants.ONCLICK_EVENT, sb.toString(), null);
	}

	private void encodeCssClasses(ResponseWriter writer, Tab tab) throws IOException {
		if (tab.isActiveTab()) {
			writer.writeAttribute(HtmlConstants.CLASS_ATTR, getDefaultCssClassesHeaderActive(), null);
		} else if (tab.isDisabled()) {
			writer.writeAttribute(HtmlConstants.CLASS_ATTR, getDefaultCssClassesHeaderDisabled(), null);
		} else {
			writer.writeAttribute(HtmlConstants.CLASS_ATTR, getDefaultCssClassesHeaderInactive(), null);
		}
	}

	private void encodeHeaderLink(FacesContext context, ResponseWriter writer, Tab tab) throws IOException {
		writer.startElement(HtmlConstants.A_ELEMENT, null);
		if (!tab.isDisabled()) {
			writer.writeAttribute(HtmlConstants.HREF_ATTR, "#" + tab.getClientId(context), "clientId");
		}
		writer.writeText(tab.getTitle(), "title");
		writer.endElement(HtmlConstants.A_ELEMENT);
	}

	private void encodeBehaviors(FacesContext context, ResponseWriter writer, Tab tab) throws IOException {
		for (Entry<String, List<ClientBehavior>> entry : tab.getClientBehaviors().entrySet()) {
			String event = entry.getKey();
			List<ClientBehavior> behaviors = entry.getValue();
			StringBuilder script = new StringBuilder();
			ClientBehaviorContext behaviorContext = ClientBehaviorContext.createClientBehaviorContext(context, tab, event, tab.getClientId(context), null);
			for (ClientBehavior behavior : behaviors) {
				script.append(behavior.getScript(behaviorContext));
				script.append(";");
			}
			writeBehavior(context, writer, event, script.toString());
		}
	}

	private void writeBehavior(FacesContext context, ResponseWriter writer, String event, String script) throws IOException {
		if (event.equals("action")) {
			writer.writeAttribute("onclick", script, null);
		} else if (event.equals("mouseover")) {
			writer.writeAttribute("onmouseover", script, null);
		}
	}

	private String getDefaultCssClassesHeaderContainer() {
		return "ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all";
	}
	
	private String getDefaultCssClassesHeaderActive() {
		return "ui-state-default ui-corner-top ui-tabs-active ui-state-active";
	}
	
	private String getDefaultCssClassesHeaderInactive() {
		return "ui-state-default ui-corner-top";
	}
	
	private String getDefaultCssClassesHeaderDisabled() {
		return "ui-state-default ui-corner-top ui-state-disabled";
	}
}
