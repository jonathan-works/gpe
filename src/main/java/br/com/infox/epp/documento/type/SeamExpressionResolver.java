package br.com.infox.epp.documento.type;

import org.jboss.seam.core.Expressions;

public class SeamExpressionResolver implements ExpressionResolver {
	@Override
	public Expression resolve(Expression expression) {
		Object value = Expressions.instance().createValueExpression(expression.getExpression()).getValue();
		String str = value == null ? null : value.toString();
		
		if (str != null) {
			expression.setResolved(true);
			expression.setValue(str);
		}
		return expression;
	}
}
