package br.com.infox.ibpm.event;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import br.com.infox.ibpm.event.Parameter;

public class ExternalMethod {

    private final String expression;
    private final String name;
    private final String tooltip;
    private final List<Parameter> parameters;

    public ExternalMethod(Method method) {
        External external = method.getAnnotation(External.class);
        this.name = method.getName();
        this.tooltip = external.tooltip();
        this.parameters = Arrays.asList(external.value());
        this.expression = getMethodExpressionExample(method);
    }

    private String getMethodExpressionExample(Method method) {
        StringBuilder methodExpression = new StringBuilder();
        methodExpression.append("#{").append(BpmExpressionService.NAME).append(".").append(method.getName())
                .append("(");
        for (int i = 0; i < parameters.size(); i++) {
            if (i > 0) {
                methodExpression.append(",");
            }
            Parameter parameter = parameters.get(i);
            methodExpression.append(parameter.defaultValue());
        }
        methodExpression.append(")}");
        return methodExpression.toString();
    }

    public String getExpression() {
        return expression;
    }

    public String getName() {
        return name;
    }

    public String getTooltip() {
        return tooltip;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

}
