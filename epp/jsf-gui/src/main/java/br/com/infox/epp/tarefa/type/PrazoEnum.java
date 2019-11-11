package br.com.infox.epp.tarefa.type;

import java.text.MessageFormat;

import br.com.infox.core.type.Displayable;

public enum PrazoEnum implements Displayable {

    H("Hora(s)"), D("Dia(s)");

    private String label;

    PrazoEnum(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    public static String formatTempo(Integer tempo, PrazoEnum tipoPrazo) {
        String result = "";
        if (tipoPrazo == null) {
            result = MessageFormat.format("-", tempo);
        } else {
            switch (tipoPrazo) {
                case H: {
                    result = MessageFormat.format("{0}h {1}m", tempo / 60, tempo % 60);
                }
                break;
                case D:
                    result = MessageFormat.format("{0}d", tempo);
                break;
            }
        }
        return result;
    }

}
