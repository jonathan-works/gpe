package br.com.infox.epp.documento.type;

import br.com.infox.core.type.Displayable;

public enum TipoNumeracaoEnum implements Displayable {
    S("Sequencial"), J("Classe Java");

    private String label;

    TipoNumeracaoEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }
}
