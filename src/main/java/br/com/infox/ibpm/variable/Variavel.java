package br.com.infox.ibpm.variable;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

public class Variavel implements Serializable {

    private static final long serialVersionUID = 4536717298608507132L;
    private final String type;
    private final Object value;
    private final String label;

    public Variavel(String nome, Object valor, String tipo) {
        this.label = nome;
        this.value = valor;
        this.type = tipo;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    // TODO ver com Ruiz se dá para usar o componente (type) pra mostar a
    // variável... :P
    public String getValuePrint() {
        if (value instanceof Boolean) {
            Boolean var = (Boolean) value;
            if (var) {
                return "Sim";
            } else {
                return "Não";
            }
        }
        if (value instanceof Date) {
            return DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(value);
        }
        return value.toString();
    }

    public String getLabel() {
        return label;
    }

    public String toString() {
        return label + ": " + value;
    }

}
