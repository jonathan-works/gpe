package br.com.infox.ibpm.variable.components;

public interface ComponentDefinition {

	String getId();
	String getName();
	String getDescription();
	public String getXhtmlPath();
	public boolean isDisabled();
	
}
