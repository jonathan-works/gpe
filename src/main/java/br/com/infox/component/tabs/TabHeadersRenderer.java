package br.com.infox.component.tabs;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

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
		writer.startElement("ul", tabHeaders);
		for (Tab tab : panel.getTabs()) {
			if (tab.isRendered()) {
				writer.startElement("li", null);
				writer.writeAttribute("name", tab.getName(), "name");
				writer.startElement("a", null);
				writer.writeAttribute("href", "#" + tab.getClientId(), "clientId");
				writer.writeText(tab.getTitle(), "title");
				writer.endElement("a");
				writer.endElement("li");
			}
		}
		writer.endElement("ul");
		
		writer.startElement("script", null);
		StringBuffer sb = new StringBuffer();
		sb.append("function __");
		sb.append(panel.getClientId().replace(":", ""));
		sb.append("(event, ui) {");
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
		writer.endElement("script");
	}
}
