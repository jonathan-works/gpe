package br.com.infox.epp.loglab.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.loglab.contribuinte.type.ContribuinteEnum;
import br.com.infox.epp.pessoa.annotation.Cpf;
import br.com.infox.epp.pessoa.type.TipoGeneroEnum;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="matricula")
public class ContribuinteSolicitanteVO {

    private Long id;

    @NotNull
    private ContribuinteEnum tipoContribuinte;

    @NotNull
    @Cpf
    private String cpf;

    private String matricula;

    @NotNull
    @Size(min = 6, max = 256)
    private String nomeCompleto;

    @NotNull
    private TipoGeneroEnum sexo;

    @NotNull
    private Date dataNascimento;

    @NotNull
    @Size(min = 3, max = 20)
    private String numeroRg;

    @NotNull
    @Size(min = 3, max = 256)
    private String emissorRg;

    @NotNull
    private Long idEstadoRg;

    private String cdEstadoRg;

    @NotNull
    @Size(min = 6, max = 256)
    private String nomeMae;

    @NotNull
    private String email;

    private String telefone;

    private String cidade;

    private String logradouro;

    private String bairro;

    private String complemento;

    private String numero;

    private String cep;

    private String status;

}
