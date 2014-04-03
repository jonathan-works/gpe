package br.com.infox.ibpm.variable.type;

import br.com.infox.core.type.Displayable;

public enum ValidacaoDataEnum implements Displayable {

    P("Passadas"), PA("Passadas e atual"), F("Futuras"), FA("Futuras e atual"),
    G("Geral");
    
    private String label;

    private ValidacaoDataEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

}
