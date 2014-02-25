package br.com.infox.core.path;

import java.io.Serializable;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.ServletLifecycle;

@Name(PathResolver.NAME)
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class PathResolver implements Serializable{

    private static final long serialVersionUID = 1L;
    public static final String NAME = "pathResolver";

    /**
     * @return o caminho do projeto.
     */
    public String getContextPath() {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getExternalContext().getRequestContextPath();
    }
    
    public final String getContextPath(String relativePath) {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getExternalContext().getRequestContextPath()+relativePath;
    }

    /**
     * @return caminho completo do projeto desde o servidor
     */
    public String getContextRealPath() {
        return ServletLifecycle.getServletContext().getRealPath("");
    }
    
    public final String getRealPath(String relativePath) {
        return ServletLifecycle.getServletContext().getRealPath(relativePath);
    }

    public String getUrlProject() {
        HttpServletRequest rc = getRequest();
        String url = rc.getRequestURL().toString();
        String protEnd = "://";
        int pos = url.indexOf(protEnd) + protEnd.length() + 1;
        return url.substring(0, url.indexOf('/', pos)) + rc.getContextPath();
    }

    private HttpServletRequest getRequest() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null && facesContext.getExternalContext() != null) {
            Object requestObj = facesContext.getExternalContext().getRequest();
            if (requestObj instanceof HttpServletRequest) {
                return (HttpServletRequest) requestObj;
            }
        }
        return null;
    }
}
