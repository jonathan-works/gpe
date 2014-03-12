package br.com.infox.seam.context;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.hibernate.LazyInitializationException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.context.ExtendedPartialViewContext;

@Name(ContextFacade.NAME)
@Scope(ScopeType.APPLICATION)
public class ContextFacade {

    public static final String NAME = "contextFacade";
    private static final LogProvider LOG = Logging.getLogProvider(ContextFacade.class);

    public String getSessionContextsAsString() {
        return getContextsAsString(Contexts.getSessionContext());
    }

    public String getApplicationContextsAsString() {
        return getContextsAsString(Contexts.getApplicationContext());
    }

    public String getConversationContextsAsString() {
        return getContextsAsString(Contexts.getConversationContext());
    }

    private String getContextsAsString(Context context) {
        StringBuilder sb = new StringBuilder();
        for (String name : context.getNames()) {
            sb.append(name).append(" = ");
            try {
                sb.append(context.get(name));
            } catch (LazyInitializationException e) {
                LOG.warn(".getContextsAsString(context)", e);
                sb.append("----");
            }
            sb.append("<br /> \n");
        }
        return sb.toString();
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

    public static void setToEventContext(String var, Object object) {
        Contexts.getEventContext().set(var, object);
    }

    // TODO encontrar um lugar melhor para esse método
    public boolean isAjaxRequest() {
        ExtendedPartialViewContext context = ExtendedPartialViewContext.getInstance(FacesContext.getCurrentInstance());
        return context != null && context.isAjaxRequest();
    }
}
