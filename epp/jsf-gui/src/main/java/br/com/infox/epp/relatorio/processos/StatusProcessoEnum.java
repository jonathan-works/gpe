package br.com.infox.epp.relatorio.processos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString(of = "label")
public enum StatusProcessoEnum {

    A("Em andamento"),
    F("Arquivados/Finalizados");

    private String label;

}
