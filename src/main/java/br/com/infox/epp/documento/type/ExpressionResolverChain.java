package br.com.infox.epp.documento.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ExpressionResolverChain implements ExpressionResolver {

	private List<ExpressionResolver> resolvers;
	
	public ExpressionResolverChain(ExpressionResolver... resolvers) {
		this(Arrays.asList(resolvers));
	}
	
	public ExpressionResolverChain(Collection<ExpressionResolver> resolvers) {
		if (resolvers == null || resolvers.isEmpty()) {
			throw new IllegalArgumentException("A cadeia de resolvers não pode ser nula nem vazia");
		}
		this.resolvers = new ArrayList<>(resolvers);
	}
	
	@Override
	public Expression resolve(Expression expression) {
		for (ExpressionResolver resolver : resolvers) {
			Expression expr = resolver.resolve(expression);
			if (expr.isResolved()) {
				return expr;
			}
		}
		return expression;
	}
}
