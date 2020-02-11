package br.gov.mt.cuiaba.pmc.gdprev;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @EqualsAndHashCode(of="matricula") @NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Interessado {
	@XmlElement(required=true, nillable=false)
	private String cpf;
	@XmlElement(required=true, nillable=false)
	private String matricula;
	@XmlElement(required=true, nillable=false)
	private String nome;
		
}
