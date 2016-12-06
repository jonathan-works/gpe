package br.com.infox.epp.documento.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

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
	
	public static BuilderInicial builder() {
		return new BuilderExpressionResolverChain();
	}
	
	public static interface BuilderInicial {
		public BuilderDefault defaultResolver(Integer idProcesso);
		public BuilderOpcionais with(ExpressionResolver resolver);
	}
	
	public static interface BuilderDefault {
		public BuilderOpcionais executionContext(ExecutionContext executionContext);
		public BuilderOpcionais taskInstance(TaskInstance taskInstance);
		public BuilderOpcionais processInstance(ProcessInstance processInstance);
	}
	
	public static interface BuilderOpcionais {
		public BuilderOpcionais and(ExpressionResolver resolver);
		public ExpressionResolverChain build();
	}
	
	public static class BuilderExpressionResolverChain implements BuilderInicial, BuilderDefault {

		private Integer idProcesso;
		
		@Override
		public BuilderOpcionais executionContext(ExecutionContext executionContext) {
			return ExpressionResolverChainBuilder.with(new JbpmExpressionResolver(idProcesso)).and(new SeamExpressionResolver(executionContext));
		}

		@Override
		public BuilderOpcionais taskInstance(TaskInstance taskInstance) {
			return ExpressionResolverChainBuilder.with(new JbpmExpressionResolver(idProcesso)).and(new SeamExpressionResolver(taskInstance));
		}
		
		@Override
		public BuilderOpcionais processInstance(ProcessInstance processInstance) {
			return ExpressionResolverChainBuilder.with(new JbpmExpressionResolver(idProcesso)).and(new SeamExpressionResolver(processInstance));
		}
		
		@Override
		public BuilderDefault defaultResolver(Integer idProcesso) {
			this.idProcesso = idProcesso;
			return this;
		}

		@Override
		public BuilderOpcionais with(ExpressionResolver resolver) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static class ExpressionResolverChainBuilder implements BuilderOpcionais {
		private List<ExpressionResolver> resolvers;
		
		public ExpressionResolverChainBuilder(ExpressionResolver resolver) {
			this.resolvers = new ArrayList<>();
			this.resolvers.add(resolver);
		}
		
		public static ExpressionResolverChainBuilder with(ExpressionResolver resolver) {
			return new ExpressionResolverChainBuilder(resolver);
		}
		
		public static ExpressionResolverChain defaultExpressionResolverChain(Integer idProcesso, ExecutionContext executionContext) {
			return new ExpressionResolverChain(new JbpmExpressionResolver(idProcesso), new SeamExpressionResolver(executionContext));
		}
		
		public static ExpressionResolverChain defaultExpressionResolverChain(Integer idProcesso, TaskInstance taskInstance) {
			return new ExpressionResolverChain(new JbpmExpressionResolver(idProcesso), new SeamExpressionResolver(taskInstance));
		}
		
		public static ExpressionResolverChain defaultExpressionResolverChain(Integer idProcesso, ProcessInstance processInstance) {
			return new ExpressionResolverChain(new JbpmExpressionResolver(idProcesso), new SeamExpressionResolver(processInstance));
		}
		
		public ExpressionResolverChainBuilder and(ExpressionResolver resolver) {
			this.resolvers.add(resolver);
			return this;
		}
		
		public ExpressionResolverChain build() {
			return new ExpressionResolverChain(resolvers);
		}
	}
}
