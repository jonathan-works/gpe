package br.com.infox.epp.events;

import br.com.infox.epp.entrega.modelo.ModeloEntrega;

public class FimPrazoModeloEntregaEvent {

	private ModeloEntrega modeloEntrega;

	public ModeloEntrega getModeloEntrega() {
		return modeloEntrega;
	}

	public void setModeloEntrega(ModeloEntrega modeloEntrega) {
		this.modeloEntrega = modeloEntrega;
	}
	
}
