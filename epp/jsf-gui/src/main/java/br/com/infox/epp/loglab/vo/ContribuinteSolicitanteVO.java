package br.com.infox.epp.loglab.vo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.pessoa.annotation.Cpf;
import br.com.infox.util.time.Date;
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
public class ContribuinteSolicitanteVO {

	private Long id;

	@NotNull
	@Cpf
	private String cpf;

	private String matricula;

	@NotNull
	@Size(min = 6, max = 256)
	private String nomeCompleto;

	@NotNull
	private Character sexo;

	@NotNull
	private Date dataNascimento;

	@NotNull
	@Size(min = 3, max = 20)
    private String numeroRg;

	@NotNull
	@Size(min = 3, max = 256)
    private String emissorRg;

	@NotNull
    private String ufRg;

	@NotNull
	@Size(min = 6, max = 256)
	private String nomeMae;

	@NotNull
    private String email;

    private String telefone;

    private EnderecoVO enderecoVO;

}
