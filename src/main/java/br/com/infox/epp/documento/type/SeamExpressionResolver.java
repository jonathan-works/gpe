package br.com.infox.epp.documento.type;

import org.jboss.seam.core.Expressions;

public class SeamExpressionResolver implements ExpressionResolver {
	@Override
	public Expression resolve(Expression expression) {
		String value = Expressions.instance().createValueExpression(expression.getExpression()).getValue().toString();
		if (!value.isEmpty()) {
			expression.setResolved(true);
			expression.setValue(value);
		}
		return expression;
	}
}
