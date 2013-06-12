package br.com.infox.component.tabs;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIPanel;

@FacesComponent(Tab.COMPONENT_ID)
public class Tab extends UIPanel {
	public static final String COMPONENT_ID = "br.com.infox.component.tabs.Tab";
	public static final String RENDERER_TYPE = "br.com.infox.component.tabs.TabRenderer";
	public static final String COMPONENT_FAMILY = "br.com.infox.component.tabs";
	
	private static enum PropertyKeys {
		name, title;
	}
	
	@Override
	public String getRendererType() {
		return RENDERER_TYPE;
	}
	
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	
	public String getName() {
		return (String) getStateHelper().get(PropertyKeys.name);
	}
	
	public void setName(String name) {
		getStateHelper().put(PropertyKeys.name, name);
	}
	
	public String getTitle() {
		return (String) getStateHelper().get(PropertyKeys.title);
	}
	
	public void setTitle(String title) {
		getStateHelper().put(PropertyKeys.title, title);
	}
}
