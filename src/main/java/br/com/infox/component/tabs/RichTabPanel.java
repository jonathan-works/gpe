package br.com.infox.component.tabs;

import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;

import org.richfaces.component.UITabPanel;

@FacesComponent("richTabPanel")
public class RichTabPanel extends UITabPanel {
	@Override
	public String getActiveItem() {
		ValueExpression ve = getValueExpression("value");
		if (ve != null) {
			return (String) ve.getValue(getFacesContext().getELContext());
		}
		return super.getActiveItem();
	}
	
	@Override
	public void setActiveItem(String value) {
		super.setActiveItem(value);
		ValueExpression ve = getValueExpression("value");
		if (ve != null) {
			ve.setValue(getFacesContext().getELContext(), value);
		}
	}
}
