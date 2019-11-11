package br.com.infox.epp.processo.variavel.bean;

import java.io.Serializable;

public class VariavelProcesso implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nome;
    private String label;
    private String valor;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}
