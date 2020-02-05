package br.com.infox.epp.loglab.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.loglab.contribuinte.type.ContribuinteEnum;
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
public class ContribuinteSolicitanteVO {

    private Long id;

    private ContribuinteEnum tipoContribuinte;

    @NotNull
    @Cpf
    private String cpf;

    private String matricula;

    @Size(min = 6, max = 256)
    private String nomeCompleto;

    private String sexo;

    private Date dataNascimento;

    @Size(min = 3, max = 20)
    private String numeroRg;

    @Size(min = 3, max = 256)
    private String emissorRg;

    private Long idEstadoRg;

    private String cdEstadoRg;

    @Size(min = 6, max = 256)
    private String nomeMae;

    @NotNull
    private String email;

    private String telefone;

    @Size(min = 3, max = 100)
    private String cidade;

    private Long idEstado;

    private String cdEstado;

    @Size(min = 3, max = 256)
    private String logradouro;

    @Size(min = 3, max = 256)
    private String bairro;

    @Size(min = 3, max = 256)
    private String complemento;

    @Size(min = 3, max = 20)
    private String numero;

    private String cep;

}
