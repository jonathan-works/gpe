package br.com.infox.util;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.ServletLifecycle;

@Name(ImageUtil.NAME)
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class ImageUtil {

    public static final String NAME = "imageUtil";

    public ImageUtil() {
    }

    public final String getRealPath(String relativePath) {
        return ServletLifecycle.getServletContext().getRealPath(relativePath);
    }
    
    public final String getRealPath() {
        return ServletLifecycle.getServletContext().getRealPath("");
    }

    public final String getContextPath(String relativePath) {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getExternalContext().getRequestContextPath()+relativePath;
    }
    
    public final String getContextPath() {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getExternalContext().getRequestContextPath();
    }

}
