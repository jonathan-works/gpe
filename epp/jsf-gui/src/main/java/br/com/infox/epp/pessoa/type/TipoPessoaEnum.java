package br.com.infox.epp.pessoa.type;

import br.com.infox.core.type.Displayable;

public enum TipoPessoaEnum implements Displayable {

    F("Física"), J("Jurídica"), A("Anônima");

    private String label;

    TipoPessoaEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public static TipoPessoaEnum[] values(boolean exibeAnonima) {
        if(exibeAnonima) {
            return values();
        }
        return new TipoPessoaEnum[]{F, J};
    }

}
