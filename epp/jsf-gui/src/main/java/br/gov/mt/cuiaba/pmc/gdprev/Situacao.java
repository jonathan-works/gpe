package br.gov.mt.cuiaba.pmc.gdprev;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data @AllArgsConstructor @EqualsAndHashCode(of="codigo")
public class Situacao {
	private String codigo;
	private String nome;
}
