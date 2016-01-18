package br.com.infox.jsf.util;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.richfaces.component.UIDataTable;

@Named
@RequestScoped
public class JsfUtil {
    
	public static void clear(UIComponent uiComponent){
		if(uiComponent instanceof EditableValueHolder){
			((EditableValueHolder)uiComponent).resetValue();
			((EditableValueHolder)uiComponent).setValue(null);
		}
		for (UIComponent child : uiComponent.getChildren()){
			if(!(child instanceof UIData) || !(child instanceof UIDataTable)){
				clear(child);
			}
		}
	}
	
	public static void clear(String... componentIds){
	    if (componentIds == null) return;
	    FacesContext facesContext = FacesContext.getCurrentInstance();
	    for (String componentId : componentIds) {
	        UIComponent component = facesContext.getViewRoot().findComponent(componentId);
	        clear(component);
	    }
    }
	
	public void clearForm(String formId) {
	    FacesContext facesContext = FacesContext.getCurrentInstance();
        UIComponent formComponent = facesContext.getViewRoot().findComponent(formId);
        List<UIComponent> children = formComponent.getChildren();
        for (UIComponent uiComponent : children) {
            clear(uiComponent);
        }
	}
}
