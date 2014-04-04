package br.com.infox.ibpm.variable.type;

import br.com.infox.core.type.Displayable;

public enum ValidacaoDataEnum implements Displayable {

    P("Passada"), PA("Passada ou atual"), F("Futura"), FA("Futura ou atual"),
    L("Livre");
    
    private String label;

    private ValidacaoDataEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

}
