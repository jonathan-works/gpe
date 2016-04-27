package br.com.infox.jsf.function;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.text.MessageFormat;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Expressions;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(ElFunctions.NAME)
@Scope(ScopeType.APPLICATION)
public class ElFunctions {

    public static final String NAME = "elFunctions";
    private static final LogProvider LOG = Logging.getLogProvider(ElFunctions.class);

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

    /**
     * Cria um valor de expressão a partir de um método do Seam.
     * 
     * @param expression - Expressão a ser criada.
     * @return Expressão criada.
     */
    @SuppressWarnings(UNCHECKED)
    public <C> C evaluateExpression(String expression) {
        if (expression == null || expression.trim().length() == 0) {
            return null;
        }
        String expr = expression.trim();
        if (!expr.startsWith("#{")) {
            expr = "#{" + expr + "}";
        }
        return (C) Expressions.instance().createValueExpression(expr).getValue();
    }
    
    public static <C> C evaluateExpression(String expression, Class<C> type){
        if (expression == null || expression.trim().length() == 0) {
            return null;
        }
        String expr = expression.trim();
        if (!expr.startsWith("#{")) {
            expr = "#{" + expr + "}";
        }
        return Expressions.instance().createValueExpression(expr, type).getValue();
    }

}
