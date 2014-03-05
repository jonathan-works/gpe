package br.com.infox.jsf.function;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.util.Strings;

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
}
