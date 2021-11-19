package br.com.infox.epp.gdprev.vidafuncional;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(of = "label")
@AllArgsConstructor
public enum GDPrevOpcaoDownload{
    CD("Consultar Dossiê"),
    VF("Elaborar Vida Funcional"),
    TS("Relatório de Tempo de Serviço");
    private String label;
}