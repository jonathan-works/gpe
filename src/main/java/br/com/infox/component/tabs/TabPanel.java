package br.com.infox.component.tabs;

import java.util.HashMap;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIPanel;

@FacesComponent(TabPanel.COMPONENT_ID)
public class TabPanel extends UIPanel implements NamingContainer {
	public static final String COMPONENT_ID = "br.com.infox.component.tabs.TabPanel";
	public static final String RENDERER_TYPE = "br.com.infox.component.tabs.TabPanelRenderer";
	public static final String COMPONENT_FAMILY = "br.com.infox.component.tabs";
	
	private static enum PropertyKeys {
		activeTab, switchType, tabIndexMap;
	}
	
	@Override
	public String getRendererType() {
		return RENDERER_TYPE;
	}
	
	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}
	
	public String getActiveTab() {
		ValueExpression ve = getValueExpression("activeTab");
		if (ve != null) {
			return (String) ve.getValue(getFacesContext().getELContext());
		}
		return (String) getStateHelper().eval(PropertyKeys.activeTab);
	}
	
	public void setActiveTab(String activeTab) {
		ValueExpression ve = getValueExpression("activeTab");
		if (ve != null) {
			ve.setValue(getFacesContext().getELContext(), activeTab);
		} else {
			getStateHelper().put(PropertyKeys.activeTab, activeTab);
		}
	}
	
	public String getSwitchType() {
		return (String) getStateHelper().get(PropertyKeys.switchType);
	}
	
	public void setSwitchType(String switchType) {
		getStateHelper().put(PropertyKeys.switchType, switchType);
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Integer> getTabIndexMap() {
		Map<String, Integer> tabIndexMap = (Map<String, Integer>) getStateHelper().get(PropertyKeys.tabIndexMap);
		if (tabIndexMap != null) {
			return tabIndexMap;
		}
		tabIndexMap = new HashMap<String, Integer>();
		setTabIndexMap(tabIndexMap);
		return tabIndexMap;
	}
	
	public void setTabIndexMap(Map<String, Integer> tabIndexMap) {
		getStateHelper().put(PropertyKeys.tabIndexMap, tabIndexMap);
	}
}
