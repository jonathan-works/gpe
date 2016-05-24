package br.com.infox.ibpm.variable.type;

import br.com.infox.core.type.Displayable;

public enum ValidacaoDataEnum implements Displayable {

    P("Passada", "pastDateOnlyValidator"), 
    PA("Passada ou atual", "pastDateValidator"), 
    F("Futura", "futureDateOnlyValidator"), 
    FA("Futura ou atual", "futureDateValidator"),
    L("Livre", null);
    
    private String label;
    private String validatorId;

    private ValidacaoDataEnum(String label, String validatorId) {
        this.label = label;
        this.validatorId = validatorId;
    }

    @Override
    public String getLabel() {
        return label;
    }
    
    public String getValidatorId() {
        return validatorId;
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
