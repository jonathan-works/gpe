package br.com.infox.ibpm.type;

import br.com.infox.type.Displayable;


public enum TarefaEventoEnum implements Displayable {

	ET("Entrar Tarefa"), RT("Realizar Tarefa"), ST("Sair Tarefa");
	
	private String label;
	
	TarefaEventoEnum(String label) {
		this.label = label;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}

}