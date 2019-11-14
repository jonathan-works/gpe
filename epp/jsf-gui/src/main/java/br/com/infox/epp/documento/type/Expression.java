package br.com.infox.epp.documento.type;

public class Expression {
	private String expression;
	private boolean resolved;
	private String value;
	private Object originalValue;
	
	public Expression(String expression) {
		if (expression == null || expression.isEmpty()) {
			throw new IllegalArgumentException("A expressão não pode ser nula nem vazia");
		}
		this.expression = expression;
	}
	
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	public boolean isResolved() {
		return resolved;
	}
	
	public void setResolved(boolean resolved) {
		this.resolved = resolved;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public Object getOriginalValue() {
		return originalValue;
	}
	
	public void setOriginalValue(Object originalValue) {
		this.originalValue = originalValue;
	}
}