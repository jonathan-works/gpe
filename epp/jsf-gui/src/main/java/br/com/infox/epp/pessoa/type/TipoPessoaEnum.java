package br.com.infox.epp.pessoa.type;

import br.com.infox.core.type.Displayable;

public enum TipoPessoaEnum implements Displayable {

    F("Física"), J("Jurídica");

    private String label;

    TipoPessoaEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}
