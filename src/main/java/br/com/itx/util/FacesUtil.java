package br.com.itx.util;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 * Classe genérica para acesso ao container do myfaces.
 */
public final class FacesUtil {

    private FacesUtil() {
    }

    /**
     * Recupera um ServltContext do builder.
     * 
     * @param webapp define o contexto a ser recuperado.
     */
    public static ServletContext getServletContext(String webapp) {
        FacesContext fc = FacesContext.getCurrentInstance();
        ExternalContext ec = fc.getExternalContext();
        ServletContext wiSc = (ServletContext) ec.getContext();
        if (webapp == null) {
            return wiSc;
        }
        return wiSc.getContext(webapp);
    }

}
