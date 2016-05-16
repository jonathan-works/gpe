package br.com.infox.jsf.util;

import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.richfaces.component.UIDataTable;

import br.com.infox.epp.cdi.config.BeanManager;

@Named
@RequestScoped
public class JsfUtil {
    
    private transient FacesContext context;
    
    @PostConstruct
    private void init() {
        context = FacesContext.getCurrentInstance();
        if (context == null) {
            throw new IllegalStateException("FacesContext is null");
        }
    }
    
    public static JsfUtil instance() {
        return BeanManager.INSTANCE.getReference(JsfUtil.class);
    }
    
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
        UIComponent formComponent = context.getViewRoot().findComponent(formId);
        List<UIComponent> children = formComponent.getChildren();
        for (UIComponent uiComponent : children) {
            clear(uiComponent);
        }
	}
	
    public void render(String clientId) {
        context.getPartialViewContext().getRenderIds().add(clientId);
    }

    public void render(Collection<String> collection) {
        context.getPartialViewContext().getRenderIds().addAll(collection);
    }
    
    public void execute(String script) {
        RequestContext.getCurrentInstance().execute(script);
    }
    
    public void addFlashParam(String name, Object value) {
        context.getExternalContext().getFlash().put(name, value);
    }
    
    public <T> T getFlashParam(String name, Class<T> clazz) {
        return clazz.cast(context.getExternalContext().getFlash().get(name));
    }
}
