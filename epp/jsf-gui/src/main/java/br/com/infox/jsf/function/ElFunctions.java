package br.com.infox.jsf.function;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
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
        String expr = expression.trim();
        if (!expr.startsWith("#{")) {
            expr = "#{" + expr + "}";
        }
        return (C) Expressions.instance().createValueExpression(expr).getValue();
    }
    
    public String evaluateExpressions(String texto){
        StringBuilder textoSaida = new StringBuilder();
        String[] linhas = texto.split("\n");

        StringBuffer sb = new StringBuffer();
        Pattern pattern = Pattern.compile("#[{][^{}]+[}]");

        for (int i = 0; i < linhas.length; i++) {
            if (textoSaida.length() > 0) {
                textoSaida.append('\n');
                sb.delete(0, sb.length());
            }

            Matcher matcher = pattern.matcher(linhas[i]);

            while (matcher.find()) {
                String group = matcher.group();
                String expression = group;
                if (expression == null) {
                        expression = group;
                }
                expression = StringEscapeUtils.unescapeHtml4(expression);
                String value = Expressions.instance().createValueExpression(expression).getValue().toString();
                // Os caracteres \ e $ devem ser escapados devido ao funcionamento do método
                // Matcher#appendReplacement (ver o Javadoc correspondente).
                // Importante manter a ordem dos replaces abaixo
                value = value == null ? "" : value;
                value = value.replace("\\", "\\\\").replace("$", "\\$");
                matcher.appendReplacement(sb, value);
            }
            matcher.appendTail(sb);
            textoSaida.append(sb.toString());
        }
        return textoSaida.toString();
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
