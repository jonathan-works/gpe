package br.com.infox.epp.documento.type;

import br.com.infox.core.type.Displayable;

public enum VisibilidadeEnum implements Displayable {

    A("Ambos"), I("Interno"), E("Externo");

    private String label;

    VisibilidadeEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}
