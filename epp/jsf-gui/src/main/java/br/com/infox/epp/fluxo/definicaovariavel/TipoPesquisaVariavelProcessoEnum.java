package br.com.infox.epp.fluxo.definicaovariavel;

import br.com.infox.core.type.Displayable;
import lombok.Getter;

public enum TipoPesquisaVariavelProcessoEnum implements Displayable {

    D("Data"),
    I("Numérico"),
    N("Monetário"),
    B("Booleano"),
    T("Texto")
    ;

    @Getter
    private String label;

    TipoPesquisaVariavelProcessoEnum(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return getLabel();
    }

}