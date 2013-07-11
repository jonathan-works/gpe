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
		writer.writeAttribute(HtmlConstants.CLASS_ATTR, getDefaultCssClassesHeaderContainer(), null);
		for (Tab tab : panel.getTabs()) {
			if (tab.isRendered()) {
				writer.startElement(HtmlConstants.LI_ELEMENT, null);
				writer.writeAttribute(HtmlConstants.NAME_ATTR, tab.getName(), "name");
				encodeBehaviors(context, writer, tab);
				if (tab.isActiveTab()) {
					writer.writeAttribute(HtmlConstants.CLASS_ATTR, getDefaultCssClassesHeaderActive(), null);
				} else if (tab.isDisabled()) {
					writer.writeAttribute(HtmlConstants.CLASS_ATTR, getDefaultCssClassesHeaderDisabled(), null);
				} else {
					writer.writeAttribute(HtmlConstants.CLASS_ATTR, getDefaultCssClassesHeaderInactive(), null);
				}
				writer.startElement(HtmlConstants.A_ELEMENT, null);
				writer.writeAttribute(HtmlConstants.HREF_ATTR, "#" + tab.getClientId(context), "clientId");
				writer.writeText(tab.getTitle(), "title");
				writer.endElement(HtmlConstants.A_ELEMENT);
				writer.endElement(HtmlConstants.LI_ELEMENT);
			}
		}
		writer.endElement(HtmlConstants.UL_ELEMENT);
		
		writer.startElement(HtmlConstants.SCRIPT_ELEMENT, null);
		StringBuffer sb = new StringBuffer();
		sb.append("function __");
		sb.append(panel.getClientId().replace(":", ""));
		sb.append("(event, ui) {");
		sb.append("event.type = 'activateTab';");
		sb.append("jsf.ajax.request(ui.oldPanel[0], ");
		sb.append("event, {render: '");
		sb.append(panel.getClientId(context));
		sb.append("',");
		sb.append("newTab: ");
		sb.append("ui.newTab.attr('name')");
		sb.append("});");
		sb.append("}");
		writer.writeText(sb.toString(), null);
		writer.endElement(HtmlConstants.SCRIPT_ELEMENT);
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
