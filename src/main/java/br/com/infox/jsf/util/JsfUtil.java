package br.com.infox.jsf.util;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.primefaces.context.RequestContext;
import org.richfaces.component.UIDataTable;

import com.sun.faces.context.flash.ELFlash;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.seam.exception.ApplicationException;

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
        Flash flash = context.getExternalContext().getFlash();
        flash.put(name, value);
    }
    
    public void applyLastPhaseFlashAction() {
        ELFlash flash = (ELFlash) context.getExternalContext().getFlash();
        flash.doLastPhaseActions(context, true);
    }
    
    public <T> T getFlashParam(String name, Class<T> clazz) {
        return clazz.cast(context.getExternalContext().getFlash().get(name));
    }
    
    public void redirect(String path) {
        ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
        try {
            context.getExternalContext().redirect(servletContext.getContextPath() + path);
        } catch (IOException e) {
            throw new ApplicationException("Path does not exists '" + path + "' ", e);
        }
    }
}
