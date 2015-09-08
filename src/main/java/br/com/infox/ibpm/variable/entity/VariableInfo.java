package br.com.infox.ibpm.variable.entity;

import br.com.infox.ibpm.process.definition.variable.VariableType;

public class VariableInfo {
	
	private String mappedName;
	private String variableName;
	private VariableType variableType;
	
	public VariableInfo(String mappedName) {
		this.mappedName = mappedName;
		String[] split = mappedName.split(":");
		this.variableName = split[1];
		this.variableType = VariableType.valueOf(split[0]);
	}
	
	public String getMappedName() {
		return mappedName;
	}
	
	public String getVariableName() {
		return variableName;
	}
	
	public VariableType getVariableType() {
		return variableType;
	}
}
