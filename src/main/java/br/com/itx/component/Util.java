package br.com.itx.component;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;
import org.richfaces.context.ExtendedPartialViewContext;

@Scope(ScopeType.APPLICATION)
public class Util implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(Util.class);

    /**
     * Retorna o caminho do projeto.
     * 
     * @return
     */
    public String getContextPath() {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc.getExternalContext().getRequestContextPath();
    }

    /**
     * Retorna o caminho completo, ou seja, desde o servidor.
     * 
     * @return
     */
    public String getContextRealPath() {
        return ServletLifecycle.getServletContext().getRealPath("");
    }

    public String getUrlProject() {
        HttpServletRequest rc = getRequest();
        String url = rc.getRequestURL().toString();
        String protEnd = "://";
        int pos = url.indexOf(protEnd) + protEnd.length() + 1;
        return url.substring(0, url.indexOf('/', pos)) + rc.getContextPath();
    }

    public HttpServletRequest getRequest() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext != null && facesContext.getExternalContext() != null) {
            Object requestObj = facesContext.getExternalContext().getRequest();
            if (requestObj instanceof HttpServletRequest) {
                return (HttpServletRequest) requestObj;
            }
        }
        return null;
    }

    /**
     * @return Retorna o diretório do JSF View Id, ou seja, o diretório da
     *         página atual.
     */
    public String getViewIdDirectory() {
        FacesContext fc = FacesContext.getCurrentInstance();
        String viewId = fc.getViewRoot().getViewId();
        return viewId.substring(0, viewId.lastIndexOf('/') + 1);
    }

    /**
     * Gera uma lista de SelectItem partindo de uma String separada por vírgula
     * 
     * @param values são os valores separados por vírgulas, no formato
     *        valor:label
     * @return lista de SelectItem
     */

    // TODO Tratar virgula e dois pontos no valor ou no texto (\, \:) -> ou JSON
    public List<SelectItem> splitAsList(String values) {
        List<SelectItem> l = new ArrayList<SelectItem>();
        for (String s : values.split(",")) {
            if (s.indexOf(":") == -1) {
                l.add(new SelectItem(s));
            } else {
                String[] value = s.split(":");
                l.add(new SelectItem(value[0], value[1]));
            }
        }
        return l;
    }

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

    // TODO verificar se este metodo ficará aqui mesmo, pois no
    // SelectsItensQuery não estava funcionando
    /**
     * Método que trata a expressão a ser mostrada nas opções chamadas pelo
     * componente s:selectItems
     * 
     * @param expression é a expressão no formato {campo}, onde campo é o nome
     *        de um atributo da entidade a ser mostrada.
     * @param obj é a instância do objeto em cada uma das opções, corresponde ao
     *        atributo var do componente s:selectItems
     * @return
     */
    public Object getSelectExpressionSelectItem(String expression, Object obj) {
        Object returnObject = "";
        if (!Strings.isEmpty(expression)) {
            Contexts.getMethodContext().set("obj", obj);
            String auxiliarExpression = expression.replace("{", "#{obj.");
            returnObject = obj == null ? "" : Expressions.instance().createValueExpression(auxiliarExpression).getValue();
            Contexts.getMethodContext().remove("obj");
        }
        return returnObject;
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
