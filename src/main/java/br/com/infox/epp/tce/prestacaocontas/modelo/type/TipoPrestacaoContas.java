package br.com.infox.epp.tce.prestacaocontas.modelo.type;

import br.com.infox.core.type.Displayable;

public enum TipoPrestacaoContas implements Displayable {
    GOV("Governo"), GES("Gestão");
    
    private String label;

    private TipoPrestacaoContas(String label) {
        this.label = label;
    }
    
    @Override
    public String getLabel() {
        return label;
    }
}
