package br.com.infox.jsf.function;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;

import com.google.common.base.Strings;

import br.com.infox.core.messages.InfoxMessages;

@Name(SelectItemFunctions.NAME)
@Scope(ScopeType.APPLICATION)
public class SelectItemFunctions {

    public static final String NAME = "selectItemFunctions";

    /**
     * Gera uma lista de SelectItem partindo de uma String separada por vírgula
     * 
     * @param values são os valores separados por vírgulas, no formato
     *        valor:label
     * @return lista de SelectItem
     */

    // TODO Tratar virgula e dois pontos no valor ou no texto (\, \:) -> ou JSON
    public List<SelectItem> createFromString(String values) {
        if (Strings.isNullOrEmpty(values)) return null;
        List<SelectItem> l = new ArrayList<SelectItem>();
        for (String s : values.split(",")) {
            final String[] split = s.split(":");
            final String value = split[0];
            if (split.length < 2) {
                l.add(new SelectItem(value));
            } else {
                final String label = InfoxMessages.getInstance().get(split[1]);
                l.add(new SelectItem(value, label));
            }
        }
        return l;
    }

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
        if (!Strings.isNullOrEmpty(expression)) {
            Contexts.getMethodContext().set("obj", obj);
            String auxiliarExpression = expression.replace("{", "#{obj.");
            returnObject = obj == null ? "" : Expressions.instance().createValueExpression(auxiliarExpression).getValue();
            Contexts.getMethodContext().remove("obj");
        }
        return returnObject;
    }
}
