package br.com.infox.epp.loglab.contribuinte.type;

import br.com.infox.core.type.Displayable;

public enum ContribuinteEnum implements Displayable {

    CO("Contribuinte"), SO("Solicitante");

    private String label;

    private ContribuinteEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}
