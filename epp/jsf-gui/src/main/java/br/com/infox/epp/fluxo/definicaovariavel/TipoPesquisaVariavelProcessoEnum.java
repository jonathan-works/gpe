package br.com.infox.epp.fluxo.definicaovariavel;

import br.com.infox.core.type.Displayable;
import lombok.Getter;

public enum TipoPesquisaVariavelProcessoEnum implements Displayable {

    B("Booleano"),
    D("Data"),
    M("Decimal"),
    N("Monetário"),
    I("Numérico"),
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