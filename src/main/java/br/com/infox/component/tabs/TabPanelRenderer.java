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
		String styleClass = tabPanel.getStyleClass() != null ? tabPanel.getStyleClass() : "";
		writer.writeAttribute(HtmlConstants.CLASS_ATTR, getDefaultCssClasses() + " " + styleClass, "styleClass");
	}

	private String getDefaultCssClasses() {
		return "ui-tabs ui-widget ui-widget-content ui-corner-all";
	}

	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		super.encodeEnd(context, component);
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement(HtmlConstants.DIV_ELEMENT);
	}
}
