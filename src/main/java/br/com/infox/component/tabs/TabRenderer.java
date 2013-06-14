package br.com.infox.component.tabs;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

@FacesRenderer(componentFamily = TabRenderer.COMPONENT_FAMILY, rendererType = TabRenderer.RENDERER_TYPE)
public class TabRenderer extends Renderer {
	public static final String RENDERER_TYPE = "br.com.infox.component.tabs.TabRenderer";
	public static final String COMPONENT_FAMILY = "br.com.infox.component.tabs";
	
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		super.encodeBegin(context, component);
		Tab tab = (Tab) component;
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement("div", tab);
		writer.writeAttribute("id", tab.getId(), "id");
	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		super.encodeEnd(context, component);
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement("div");
	}
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		super.decode(context, component);
		String jsfEvent = context.getExternalContext().getRequestParameterMap().get("javax.faces.partial.event");
		if (jsfEvent != null && jsfEvent.equals("tabsbeforeactivate")) {
			Tab tab = (Tab) component;
			TabPanel panel = (TabPanel) tab.getParent();
			String newTab = context.getExternalContext().getRequestParameterMap().get("newTab");
			if (newTab != null) {
				for (UIComponent child : panel.getChildren()) {
					if (child instanceof Tab) {
						Tab newActiveTab = (Tab) child;
						if (newActiveTab.getName().equals(newTab)) {
							ActionEvent event = new ActionEvent(newActiveTab);
							event.setPhaseId(PhaseId.INVOKE_APPLICATION);
							component.queueEvent(event);
							break;
						}
					}
				}
			}
		}
	}
}
