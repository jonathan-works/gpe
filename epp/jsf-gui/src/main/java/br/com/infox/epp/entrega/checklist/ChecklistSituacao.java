package br.com.infox.epp.entrega.checklist;

import br.com.infox.core.type.Displayable;

public enum ChecklistSituacao implements Displayable {
    NIF("Não Informado"),
    CON("Conforme"),
    NCO("Não Conforme"),
    NVE("Não Verificado");

    private String label;

    private ChecklistSituacao(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
    }

    public static ChecklistSituacao[] getValues() {
        ChecklistSituacao[] values = new ChecklistSituacao[3];
        values[0] = CON;
        values[1] = NCO;
        values[2] = NVE;
        return values;
    }
}
