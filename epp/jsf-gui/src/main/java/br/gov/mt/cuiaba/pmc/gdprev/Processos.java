package br.gov.mt.cuiaba.pmc.gdprev;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
public class Processos {
	@XmlElement(required=false, nillable=false, name="processo")
	private List<Processo> processos;
}
