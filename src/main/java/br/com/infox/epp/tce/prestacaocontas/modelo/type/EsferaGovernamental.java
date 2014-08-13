package br.com.infox.epp.tce.prestacaocontas.modelo.type;

import br.com.infox.core.type.Displayable;

public enum EsferaGovernamental implements Displayable {
    M("Municipal"), E("Estadual"), F("Federal");
    
    private String label;

    private EsferaGovernamental(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
