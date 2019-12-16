package br.com.infox.epp.loglab.vo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.pessoa.annotation.Cpf;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="id")
public class ServidorVO {

	private Long id;

	@NotNull
	@Cpf
	private String cpf;

	@NotNull
	@Size(min = 6, max = 256)
	private String nomeCompleto;

	@NotNull
	@Size(min = 3, max = 256)
	private String cargoFuncao;

	@NotNull
    private String telefone;

	@NotNull
    private String email;

	@NotNull
	@Size(min = 3, max = 256)
	private String secretaria;

	@NotNull
	@Size(min = 6, max = 256)
	private String departamento;

}
