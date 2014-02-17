package br.com.infox.epp.processo.partes.type;

import br.com.infox.core.type.Displayable;

public enum ParteProcessoEnum implements Displayable {

    F("Pessoa Física"), J("Pessoa Jurídica"), A("Ambos");

    private String label;

    ParteProcessoEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

}
