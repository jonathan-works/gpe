package br.com.infox.ibpm.variable.components;

import br.com.infox.core.util.StringUtil;

public class AnnotatedParameter implements ParameterDefinition {

	private ParameterVariable parameter;
	
	public AnnotatedParameter(ParameterVariable parameter) {
		this.parameter = parameter;
	}
	
	@Override
	public String getId() {
		return parameter.id();
	}

	@Override
	public ParameterType getType() {
		return parameter.type();
	}

	@Override
	public String getDescription() {
		return StringUtil.isEmpty(parameter.description()) ? null : parameter.description();
	}
}
