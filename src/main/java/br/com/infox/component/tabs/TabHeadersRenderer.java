package br.com.infox.component.tabs;

import java.io.IOException;

import javax.faces.component.UIComponent;
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
		for (Tab tab : panel.getTabs()) {
			if (tab.isRendered()) {
				writer.startElement(HtmlConstants.LI_ELEMENT, null);
				writer.writeAttribute(HtmlConstants.NAME_ATTR, tab.getName(), "name");
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
		sb.append("jsf.ajax.request(document.getElementById('");
		sb.append(panel.getClientId());
		sb.append("'), event, {render: '");
		sb.append(panel.getClientId());
		sb.append("',");
		sb.append("newTab: ");
		sb.append("ui.newTab.attr('name')");
		sb.append("});");
		sb.append("}");
		writer.writeText(sb.toString(), null);
		writer.endElement(HtmlConstants.SCRIPT_ELEMENT);
	}
}
