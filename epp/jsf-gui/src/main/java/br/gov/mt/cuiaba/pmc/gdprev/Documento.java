package br.gov.mt.cuiaba.pmc.gdprev;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Documento {
	@XmlMimeType("application/pdf")
	private DataHandler data;
}
