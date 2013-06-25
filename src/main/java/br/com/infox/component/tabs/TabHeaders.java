package br.com.infox.component.tabs;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIOutput;

@FacesComponent(TabHeaders.COMPONENT_ID)
public class TabHeaders extends UIOutput {
	public static final String COMPONENT_ID = "br.com.infox.component.tabs.TabHeaders";
	public static final String RENDERER_TYPE = "br.com.infox.component.tabs.TabHeadersRenderer";
	public static final String COMPONENT_FAMILY = "br.com.infox.component.tabs";
	
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	
	@Override
	public String getRendererType() {
		return RENDERER_TYPE;
	}
	
	public TabPanel getTabPanel() {
		UIComponent parent = getParent();
		while (parent != null && !(parent instanceof TabPanel)) {
			parent = parent.getParent();
		}
		return (TabPanel) parent;
	}
}
