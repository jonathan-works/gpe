package br.com.infox.jbpm.action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.jboss.seam.annotations.Create;

public abstract class ActionTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    public static Set<Class<? extends ActionTemplate>> templates = new HashSet<Class<? extends ActionTemplate>>();

    private Object[] parameters;

    public abstract String getFileName();

    public abstract String getExpression();

    public abstract String getLabel();

    public abstract void extractParameters(String expression);

    protected String[] getExpressionParameters(String expression) {
        if (expression == null || expression.equals("")) {
            return new String[0];
        }
        int i = expression.indexOf('(');
        String texto = expression.substring(i + 1);
        StringTokenizer st = new StringTokenizer(texto, ",')}");
        List<String> list = new ArrayList<String>();
        while (st.hasMoreTokens()) {
            list.add(st.nextToken().trim());
        }
        String[] ret = new String[list.size()];
        return list.toArray(ret);
    }

    public Object[] getParameters() {
        return parameters;
    }

    protected void setParameters(Object[] parameters) {
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }

    public boolean isPublic() {
        return true;
    }

    @Create
    public void init() {
        templates.add(this.getClass());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getFileName()).append("=").append(getExpression()).append(",").append(getLabel());
        return sb.toString();
    }

}
