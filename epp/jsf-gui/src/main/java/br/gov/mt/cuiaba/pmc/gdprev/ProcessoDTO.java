package br.gov.mt.cuiaba.pmc.gdprev;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of="id")
public class ProcessoDTO {

    private Integer id;
    private String nrProcesso;
}
