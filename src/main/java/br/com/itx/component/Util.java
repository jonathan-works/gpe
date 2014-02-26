package br.com.itx.component;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;
import org.richfaces.context.ExtendedPartialViewContext;

@Scope(ScopeType.APPLICATION)
public class Util implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Cria um valor de expressão a partir de um método do Seam.
     * 
     * @param expression - Expressão a ser criada.
     * @return Expressão criada.
     */
    @SuppressWarnings(UNCHECKED)
    public <C> C eval(String expression) {
        if (expression == null || expression.trim().length() == 0) {
            return null;
        }
        String expr = expression.trim();
        if (!expr.startsWith("#{")) {
            expr = "#{" + expr + "}";
        }
        return (C) Expressions.instance().createValueExpression(expr).getValue();
    }

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
