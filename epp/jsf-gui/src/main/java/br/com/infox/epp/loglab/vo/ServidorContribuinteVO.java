package br.com.infox.epp.loglab.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.loglab.contribuinte.type.TipoParticipanteEnum;
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
public class ServidorContribuinteVO {

    private Long id;
    private Integer idPessoaFisica;

    private TipoParticipanteEnum tipoParticipante;

    @NotNull
    @Cpf
    private String cpf;

    private String matricula;

    @Size(min = 6, max = 256)
    private String nomeCompleto;

    private String sexo;

    private String telefone;

    @NotNull
    private String email;
    private String orgao;
    private String localTrabalho;
    private Date dataNomeacaoContratacao;
    private Date dataPosse;
    private Date dataExercicio;
    private String situacao;
    private String subFolha;
    private String jornada;
    private String ocupacaoCarreira;
    private String cargoCarreira;
    private String ocupacaoComissao;
    private String cargoComissao;
    private String nomePai;
    private String nomeMae;
    private Date dataNascimento;
    private String numeroRg;
    private Date dataEmissaoRg;
    private String orgaoEmissorRG;

    private String codEstado;
    @Size(min = 3, max = 100)
    private String cidade;
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
