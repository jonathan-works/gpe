package br.com.infox.epp.documento.type;

import java.util.Map;

public class ArbitraryExpressionResolver implements ExpressionResolver {

	private Map<String, String> variables;
	
	public ArbitraryExpressionResolver(Map<String, String> variables) {
		if (variables == null) {
			throw new NullPointerException("O mapa de variáveis não pode ser nulo");
		}
		this.variables = variables;
	}
	
	@Override
	public Expression resolve(Expression expression) {
		String value = variables.get(expression.getExpression());
		if (value != null) {
			expression.setValue(value);
			expression.setResolved(true);
			expression.setOriginalValue(value);
		}
		return expression;
	}
}
