package br.com.itx.component;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.richfaces.context.ExtendedPartialViewContext;

@Scope(ScopeType.APPLICATION)
public class Util implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Verifica se a classe é um subtipo de AbstractHome.
     * 
     * @param object - Home em execução.
     * @return True se for um subtipo de AbstractHome
     */
    //TODO verificar a remoção desse método
    public boolean isAbstractChild(Object object) {
        return object instanceof AbstractHome<?>;
    }

    public boolean isAjaxRequest() {
        ExtendedPartialViewContext context = ExtendedPartialViewContext.getInstance(FacesContext.getCurrentInstance());
        return context != null && context.isAjaxRequest();
    }

    public static void setToEventContext(String var, Object object) {
        Contexts.getEventContext().set(var, object);
    }

}
