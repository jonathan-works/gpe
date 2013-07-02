package br.com.infox.component.tabs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.ValueExpression;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

/**
 * Componente TabPanel. Representa o painel onde estão as abas e seus cabeçalhos.
 * @author gabriel
 *
 */
@FacesComponent(TabPanel.COMPONENT_ID)
@ResourceDependencies({
		@ResourceDependency(library = "stylesheet", name = "jquery-ui.css"),
		@ResourceDependency(library = "stylesheet", name = "tabs.css"),
		@ResourceDependency(name = "jquery.js"),
		@ResourceDependency(library = "js", name = "jquery-ui.js"),
		@ResourceDependency(library = "javax.faces", name = "jsf.js") })
public class TabPanel extends UIComponentBase implements NamingContainer {
	public static final String COMPONENT_ID = "br.com.infox.component.tabs.TabPanel";
	public static final String RENDERER_TYPE = "br.com.infox.component.tabs.TabPanelRenderer";
	public static final String COMPONENT_FAMILY = "br.com.infox.component.tabs";

	/**
	 * Representa as properties do componente.
	 * activeTab pode ser o nome de uma aba ou uma ValueExpression de String que guarde/retorne o nome de uma aba. 
	 * switchType pode ser client ou ajax.
	 * tabIndexMap é usado internamente.
	 * @author gabriel
	 *
	 */
	private static enum PropertyKeys {
		activeTab, switchType, tabIndexMap, style, styleClass;
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
		String activeTab;
		ValueExpression ve = getValueExpression("activeTab");
		if (ve != null) {
			activeTab = (String) ve.getValue(getFacesContext().getELContext());
		} else {
			activeTab = (String) getStateHelper().eval(PropertyKeys.activeTab);
		}
		if (!isValidTab(activeTab)) {
			activeTab = getFirstRenderedTab().getName();
			setActiveTab(activeTab);
		}
		return activeTab;
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
		String switchType = (String) getStateHelper().get(PropertyKeys.switchType);
		if (switchType == null) {
			switchType = "ajax";
			setSwitchType(switchType);
		}
		return switchType;
	}

	public void setSwitchType(String switchType) {
		getStateHelper().put(PropertyKeys.switchType, switchType);
	}
	
	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style);
	}
	
	public void setStyle(String style) {
		getStateHelper().put(PropertyKeys.style, style);
	}
	
	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass);
	}
	
	public void setStyleClass(String styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, styleClass);
	}

	/**
	 * Constroi um mapa com o nome e os índices das abas, para ser utilizado pelo jQuery UI no parâmetro active.
	 * Utilizado internamente.
	 * @return mapa com os nomes e os índices das abas
	 */
	@SuppressWarnings("unchecked")
	Map<String, Integer> getTabIndexMap() {
		Map<String, Integer> tabIndexMap = (Map<String, Integer>) getStateHelper()
				.get(PropertyKeys.tabIndexMap);
		if (tabIndexMap != null) {
			return tabIndexMap;
		}
		tabIndexMap = new HashMap<String, Integer>();
		int index = 0;
		for (Tab tab : getTabs()) {
			if (tab.isRendered()) {
				tabIndexMap.put(tab.getName(), index++);
			}
		}
		setTabIndexMap(tabIndexMap);
		return tabIndexMap;
	}

	void setTabIndexMap(Map<String, Integer> tabIndexMap) {
		getStateHelper().put(PropertyKeys.tabIndexMap, tabIndexMap);
	}

	public List<Tab> getTabs() {
		List<Tab> tabs = new ArrayList<>();
		for (UIComponent child : getChildren()) {
			if (child instanceof Tab) {
				tabs.add((Tab) child);
			}
		}
		return tabs;
	}

	public Tab getTab(String name) {
		for (Tab tab : getTabs()) {
			if (tab.getName().equals(name)) {
				return tab;
			}
		}
		return null;
	}
	
	public boolean isValidTab(String tabName){
		Tab tab = getTab(tabName);
		return tab != null && tab.isRendered() && !tab.isDisabled();
	}
	
	public Tab getFirstRenderedTab(){
		for (Tab tab : getTabs()){
			if (tab.isRendered() && !tab.isDisabled())
				return tab;
		}
		return null;
	}
}
