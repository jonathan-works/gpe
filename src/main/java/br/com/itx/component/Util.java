package br.com.itx.component;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.text.MessageFormat;

import javax.faces.context.FacesContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.richfaces.context.ExtendedPartialViewContext;

@Scope(ScopeType.APPLICATION)
public class Util implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(Util.class);

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

    /**
     * Cria um method expression para a string informada no parametro.
     * 
     * @param methodName Método a ser chamado
     * @return MethodExpression
     */
    public static void invokeMethod(String action) {
        if (action != null && !"".equals(action)) {
            StringBuilder sb = new StringBuilder();
            sb.append("#{").append(action).append("}");
            Expressions.instance().createMethodExpression(sb.toString()).invoke();
            LOG.info(MessageFormat.format("invokeMethod: {0}", sb));
        }
    }

}
