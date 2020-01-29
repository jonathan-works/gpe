package br.com.infox.epp.loglab.contribuinte.type;

import br.com.infox.core.type.Displayable;

public enum TipoParticipanteEnum implements Displayable {

    CO("Contribuinte"), SE("Servidor");

    private String label;

    private TipoParticipanteEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}
