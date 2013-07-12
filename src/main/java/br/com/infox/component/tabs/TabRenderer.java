package br.com.infox.component.tabs;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.render.FacesRenderer;
import javax.faces.render.Renderer;

import br.com.infox.component.HtmlConstants;

@FacesRenderer(componentFamily = TabRenderer.COMPONENT_FAMILY, rendererType = TabRenderer.RENDERER_TYPE)
public class TabRenderer extends Renderer {
	public static final String RENDERER_TYPE = "br.com.infox.component.tabs.TabRenderer";
	public static final String COMPONENT_FAMILY = "br.com.infox.component.tabs";
	
	@Override
	public boolean getRendersChildren() {
		return true;
	}
	
	@Override
	public void encodeBegin(FacesContext context, UIComponent component) throws IOException {
		super.encodeBegin(context, component);
		Tab tab = (Tab) component;
		if (tab.shouldProcess()) {
			doEncodeBegin(context, tab);
		}
	}

	private void doEncodeBegin(FacesContext context, Tab tab) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement(HtmlConstants.DIV_ELEMENT, tab);
		writer.writeAttribute(HtmlConstants.ID_ATTR, tab.getClientId(context), "clientId");
		if (tab.getStyle() != null) {
			writer.writeAttribute(HtmlConstants.STYLE_ATTR, tab.getStyle(), "style");
		}
		String styleClass = tab.getStyleClass() != null ? tab.getStyleClass() : "";
		writer.writeAttribute(HtmlConstants.CLASS_ATTR, getDefaultCssClasses() + " " + styleClass, "styleClass");
	}

	private String getDefaultCssClasses() {
		return "ui-tabs-panel ui-widget-content ui-corner-bottom";
	}
	
	@Override
	public void encodeChildren(FacesContext context, UIComponent component) throws IOException {
		if (context == null || component == null) {
            throw new NullPointerException();
        }
		Tab tab = (Tab) component;
        if (tab.shouldProcess()) {
        	doEncodeChildren(context, tab);
        }
	}
	
	private void doEncodeChildren(FacesContext context, Tab tab) throws IOException {
		if (tab.getChildCount() > 0) {
            for (UIComponent child : tab.getChildren()) {
                child.encodeAll(context);
            }
        }
	}
	
	@Override
	public void encodeEnd(FacesContext context, UIComponent component) throws IOException {
		super.encodeEnd(context, component);
		Tab tab = (Tab) component;
		if (tab.shouldProcess()) {
			doEncodeEnd(context, tab);
		} else {
			writePlaceholder(context, tab);
		}
	}

	private void writePlaceholder(FacesContext context, Tab tab) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.startElement(HtmlConstants.DIV_ELEMENT, tab);
		writer.writeAttribute(HtmlConstants.ID_ATTR, tab.getClientId(context), "clientId");
		writer.writeAttribute(HtmlConstants.STYLE_ATTR, "display: none", null);
		writer.endElement(HtmlConstants.DIV_ELEMENT);
	}

	private void doEncodeEnd(FacesContext context, Tab tab) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		writer.endElement(HtmlConstants.DIV_ELEMENT);
	}
	
	@Override
	public void decode(FacesContext context, UIComponent component) {
		super.decode(context, component);
		Tab tab = (Tab) component;
		if (!tab.shouldProcess()) {
			return;
		}
		
		String source = context.getExternalContext().getRequestParameterMap().get("javax.faces.source");
		if (source == null || !source.equals(tab.getClientId(context))) {
			return;
		}
		
		decodeBehaviors(context, tab);
		
		String newTab = context.getExternalContext().getRequestParameterMap().get("newTab");
		String jsfEvent = context.getExternalContext().getRequestParameterMap().get("javax.faces.partial.event");
		if (newTab != null && jsfEvent != null && jsfEvent.equals("activateTab")) {
			TabPanel tabPanel = tab.getTabPanel();
			tabPanel.setActiveTab(newTab);
			Tab newActiveTab = tabPanel.getTab(newTab);
			ActionEvent event = new ActionEvent(newActiveTab);
			if (tab.isImmediate()) {
				event.setPhaseId(PhaseId.APPLY_REQUEST_VALUES);
			} else {
				event.setPhaseId(PhaseId.INVOKE_APPLICATION);
			}
			component.queueEvent(event);
		}
	}

	private void decodeBehaviors(FacesContext context, Tab tab) {
		Map<String, List<ClientBehavior>> behaviors = tab.getClientBehaviors();
		if (behaviors.isEmpty()) {
			return;
		}
		
		String behaviorEvent = context.getExternalContext().getRequestParameterMap().get("javax.faces.behavior.event");
		if (behaviorEvent != null) {
			List<ClientBehavior> behaviorsForEvent = behaviors.get(behaviorEvent);
			if (!behaviorsForEvent.isEmpty()) {
				String behaviorSource = context.getExternalContext().getRequestParameterMap().get("javax.faces.source");
				String clientId = tab.getClientId(context);
				if (behaviorSource != null && clientId.equals(behaviorSource)) {
					for (ClientBehavior behavior : behaviorsForEvent) {
						behavior.decode(context, tab);
					}
				}
			}
		}
	}
}
