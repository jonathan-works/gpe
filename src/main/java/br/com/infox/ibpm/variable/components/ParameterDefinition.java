package br.com.infox.ibpm.variable.components;

public interface ParameterDefinition {
		
    public static enum ParameterType {
    	
    	STRING("String"), INTEGER("Integer"), BOOLEAN("Boolean");
    	
    	private String name;
    	
    	private ParameterType(String name) {
    		this.name = name;
		}
    	
    	@Override
    	public String toString() {
    		return name;
    	}

		public String getName() {
			return name;
		}
    }
    
	public String getId();

	public ParameterType getType();

	public String getDescription();
}
