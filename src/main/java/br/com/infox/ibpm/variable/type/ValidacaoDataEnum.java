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
    
    public String getProperty() {
        if (this.equals(P)) {
            return "pastOnly";
        } else if (this.equals(PA)) {
            return "past";
        } else if (this.equals(F)) {
            return "futureOnly";
        } else if (this.equals(FA)) {
            return "future";
        } else {
            return "";
        }
    }

}
