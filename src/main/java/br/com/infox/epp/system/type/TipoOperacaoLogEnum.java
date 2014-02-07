package br.com.infox.epp.system.type;

import br.com.infox.core.type.Displayable;

public enum TipoOperacaoLogEnum implements Displayable {

    I("Insert"), D("Delete"), U("Update"), S("Select");

    private String label;

    TipoOperacaoLogEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

}
