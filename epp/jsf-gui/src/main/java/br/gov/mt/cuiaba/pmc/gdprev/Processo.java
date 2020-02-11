package br.gov.mt.cuiaba.pmc.gdprev;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor @EqualsAndHashCode(of="numero")
@XmlAccessorType(XmlAccessType.FIELD)
public class Processo {
	//número do processo
	@XmlElement(required=true, nillable=false)
	private String numero;
	@XmlElement(required=true, nillable=false)
	private String natureza;
	@XmlElement(required=true, nillable=false)
	private String categoria;
	//tipo do processo (assunto)
	@XmlElement(required=true, nillable=false)
	private String tipo;
	//tipo do processo (assunto)
	@XmlElement(required=true, nillable=false)
	private String situacao;
	//Interessados do processo
	@XmlElement(required=true, nillable=false, name="interessado")
	@XmlElementWrapper(name="interessados")
	private List<Interessado> interessados;
	//data de criação
	@XmlElement(required=true, nillable=false)
	private String dataCriacao;
	//data de encerramento ou arquivamento
	@XmlElement(required=true, nillable=false)
	private String dataEncerramento;
}
