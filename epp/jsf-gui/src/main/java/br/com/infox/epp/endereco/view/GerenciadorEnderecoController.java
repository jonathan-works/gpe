package br.com.infox.epp.endereco.view;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class GerenciadorEnderecoController implements Serializable {

	private static final long serialVersionUID = 1L;

	@Getter
	@Setter
	private EnderecoVO enderecoVO;

}
