package br.com.infox.epp.endereco.view;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(of="idContribuinteServidor")
public class EnderecoVO {

	private Long idContribuinteServidor;
	private String cidade;
	private String logradouro;
	private String bairro;
	private String complemento;
	private String numero;
	private String cep;

}
