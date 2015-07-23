package br.com.infox.epp.processo.comunicacao;

import br.com.infox.core.type.Displayable;

public enum MeioExpedicao implements Displayable {
    EM("Email"), DO("Diário Oficial"), SI("Via Sistema (eletrônica)"), IM("Impressão");

    private String label;

    private MeioExpedicao(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String toString() {
        return label;
    }
}
