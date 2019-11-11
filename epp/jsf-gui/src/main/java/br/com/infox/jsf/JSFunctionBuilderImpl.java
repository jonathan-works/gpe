package br.com.infox.jsf;

import static br.com.infox.core.util.ObjectUtil.is;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class JSFunctionBuilderImpl implements JSFunctionBuilder {
	
	private String name;
	private Set<String> args;
	private List<String> statements;
	
	JSFunctionBuilderImpl(){
		this.args = new HashSet<>();
		this.statements = new ArrayList<>();
	}

	public JSFunctionBuilder name(String name) {
		this.name = name;
		return this;
	}

	public JSFunctionBuilder arg(String arg) {
		this.args.add(arg);
		return this;
	}

	public JSFunctionBuilder statement(String statement) {
		this.statements.add(statement);
		return this;
	}

	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append("function");
		if (is(this.name).notEmpty()){
			sb.append(" ").append(this.name);
		}
		sb.append("(");
		if (is(this.args).notEmpty()){
			boolean written=false;
			for (String arg : args) {
				if (is(arg).notEmpty()){
					if (written){
						sb.append(",");
					}
					sb.append(arg);
					written=true;
				}
			}
		}
		sb.append(")").append("{");
		for (String statement : statements) {
			if (is(statement).notEmpty()){
				sb.append(statement);
			}
		}
		if (!sb.toString().trim().endsWith(";"))
		    sb.append(";");
		sb.append("}");
		return sb.toString();
	}
	
}