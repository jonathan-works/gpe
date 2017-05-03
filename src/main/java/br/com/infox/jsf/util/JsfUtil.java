package br.com.infox.jsf.util;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIData;
import javax.faces.component.UIForm;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.context.Flash;
import javax.inject.Named;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

import org.primefaces.context.RequestContext;
import org.richfaces.component.UIDataTable;

import com.sun.faces.context.flash.ELFlash;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.seam.exception.ApplicationException;

@Named
@RequestScoped
public class JsfUtil {
    
    private static final String AJAX_REDIRECT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><partial-response><redirect url=\"%s\"></redirect></partial-response>";

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
		    if (uiComponent instanceof HtmlInputHidden) {
		        ((EditableValueHolder)uiComponent).resetValue();
		    } else {
                ((EditableValueHolder) uiComponent).resetValue();
                ((EditableValueHolder)uiComponent).setValue(null);
		    }
		}
		for (UIComponent child : uiComponent.getChildren()){
			if(!(child instanceof UIData) || !(child instanceof UIDataTable)){
				clear(child);
			}
		}
	}
	
	public void clear(String componentId){
		if (componentId == null || componentId.isEmpty()) return;
			UIComponent component = context.getViewRoot().findComponent(componentId);
			if(component != null)
				clear(component);
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
        clearForm(formComponent);
	}
	
	public void clearForm() {
	    UIComponent component = UIComponent.getCurrentComponent(context);
	    while ( component != null && !(component instanceof UIForm) ) {
	        component = component.getParent();
	    }
	    if ( component != null ) {
	        clearForm(component);
	    }
	}
	
	private void clearForm(UIComponent formComponent) {
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
    
    public String getRequestParameter(String name) {
        return context.getExternalContext().getRequestParameterMap().get(name);
    }
    
    public void redirect(String path) {
        ServletContext servletContext = (ServletContext) context.getExternalContext().getContext();
        try {
            if (isAjaxRequest(context.getExternalContext())) {
                HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
                response.reset();
                response.getWriter().printf(AJAX_REDIRECT, servletContext.getContextPath() + (path.startsWith("/") ? path : "/".concat(path)));
                response.getWriter().flush();
                context.responseComplete();
            } else {
                context.getExternalContext().redirect(servletContext.getContextPath() + path);
            }
        } catch (IOException e) {
            throw new ApplicationException("Path does not exists '" + path + "' ", e);
        }
    }
    
    public boolean isAjaxRequest(ExternalContext externalContext) {
        return "partial/ajax".equals(externalContext.getRequestHeaderMap().get("faces-request"));
    }
    
    public void setRequestValue(String key, Object value) {
        context.getExternalContext().getRequestMap().put(key, value);
    }
    
    public <T> T getRequestValue(String key, Class<T> clazz) {
        Object object = context.getExternalContext().getRequestMap().get(key);
        return clazz.cast(object);
    }

    public String[] getRequestParameterValues(String key) {
        return context.getExternalContext().getRequestParameterValuesMap().get(key);
    }

    public void openPopUp(String name, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append("window.open('").append(url).append("', ");
        sb.append("'").append(name.replaceAll(Pattern.quote("'"), "\\'")).append("'").append(", ");
        sb.append("[");
        sb.append("'width=',outerWidth,");
        sb.append("',height=',outerHeight,");
        sb.append("',top=',screen.top || window['screenY'] || window['screenTop'] || 0,");
        sb.append("',left=',screen.left || window['screenX'] || window['screenLeft'] || 0,");
        sb.append("',resizable=YES',");
        sb.append("',scrollbars=YES',");
        sb.append("',status=NO',");
        sb.append("',location=NO'");
        sb.append("].join(''));");
        execute(sb.toString());
    }
    public void closeDialog(){
        RequestContext.getCurrentInstance().closeDialog(null);
    }
    public void closeDialog(Object data){
        RequestContext.getCurrentInstance().closeDialog(data);
    }
}
