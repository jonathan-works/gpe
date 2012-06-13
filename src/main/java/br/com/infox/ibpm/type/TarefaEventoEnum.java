package br.com.infox.ibpm.type;


public enum TarefaEventoEnum {

	ET("Entrar Tarefa"), RT("Realizar Tarefa"), ST("Sair Tarefa");
	
	private String label;
	
	TarefaEventoEnum(String label) {
		this.label = label;
	}
	
	public String getLabel() {
		return this.label;
	}

}