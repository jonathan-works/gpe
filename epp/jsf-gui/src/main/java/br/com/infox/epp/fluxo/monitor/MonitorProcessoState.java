package br.com.infox.epp.fluxo.monitor;

import br.com.infox.core.type.Displayable;

public enum MonitorProcessoState implements Displayable {

    OK("OK"),
    ERROR("Erro");

    private String label;

    private MonitorProcessoState(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }
}
