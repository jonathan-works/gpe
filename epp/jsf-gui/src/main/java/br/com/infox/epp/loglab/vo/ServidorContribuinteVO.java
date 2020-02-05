package br.com.infox.epp.loglab.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;

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
    private String nomeCompleto;
    private String sexo;
    private Date dataNascimento;
    private String nomeMae;
    @NotNull
    private String email;
    private String celular;
    private String codEstado;
    private String cidade;
    private String logradouro;
    private String bairro;
    private String complemento;
    private String numero;
    private String cep;

    private Date dataNomeacao;
    private Date dataPosse;
    private Date dataExercicio;
    private String situacao;
    private String orgao;
    private String localTrabalho;
    private String subFolha;
    private String jornada;
    private String ocupacaoCarreira;
    private String cargoCarreira;
    private String ocupacaoComissao;
    private String cargoComissao;
    private String nomePai;
    private String numeroRg;
    private Date dataEmissaoRg;
    private String orgaoEmissorRG;

    /**
     * Construtor para Contribuinte
     */
    public ServidorContribuinteVO(Long id, Integer idPessoaFisica, String cpf, String nomeCompleto, Date dataNascimento,
            String sexo, String nomeMae, String email, String celular, String codEstado, String cidade,
            String logradouro, String bairro, String complemento, String numero, String cep) {
        this.id = id;
        this.idPessoaFisica = idPessoaFisica;
        this.cpf = cpf;
        this.nomeCompleto = nomeCompleto;
        this.sexo = sexo;
        this.dataNascimento = dataNascimento;
        this.nomeMae = nomeMae;
        this.email = email;
        this.celular = celular;
        this.codEstado = codEstado;
        this.cidade = cidade;
        this.logradouro = logradouro;
        this.bairro = bairro;
        this.complemento = complemento;
        this.numero = numero;
        this.cep = cep;
        this.tipoParticipante = TipoParticipanteEnum.CO;
    }

    /**
     * Construtor para Servidor
     */
    public ServidorContribuinteVO(Long id, Integer idPessoaFisica, String cpf, String nomeCompleto, Date dataNascimento,
            String matricula, String nomeMae, String email, String celular, Date dataNomeacao, Date dataPosse,
            Date dataExercicio, String situacao, String orgao, String localTrabalho, String subFolha, String jornada,
            String ocupacaoCarreira, String cargoCarreira, String ocupacaoComissao, String cargoComissao,
            String nomePai, String numeroRg, Date dataEmissaoRg, String orgaoEmissorRG) {
        this.id = id;
        this.idPessoaFisica = idPessoaFisica;
        this.cpf = cpf;
        this.matricula = matricula;
        this.nomeCompleto = nomeCompleto;
        this.dataNascimento = dataNascimento;
        this.nomeMae = nomeMae;
        this.email = email;
        this.celular = celular;
        this.dataNomeacao = dataNomeacao;
        this.dataPosse = dataPosse;
        this.dataExercicio = dataExercicio;
        this.situacao = situacao;
        this.orgao = orgao;
        this.localTrabalho = localTrabalho;
        this.subFolha = subFolha;
        this.jornada = jornada;
        this.ocupacaoCarreira = ocupacaoCarreira;
        this.cargoCarreira = cargoCarreira;
        this.ocupacaoComissao = ocupacaoComissao;
        this.cargoComissao = cargoComissao;
        this.nomePai = nomePai;
        this.numeroRg = numeroRg;
        this.dataEmissaoRg = dataEmissaoRg;
        this.orgaoEmissorRG = orgaoEmissorRG;
        this.tipoParticipante = TipoParticipanteEnum.SE;
    }

    /**
     * Construtor com dados básicos de Pessoa Física para Contribuinte
     */
    public ServidorContribuinteVO(Integer idPessoaFisica, String cpf, String nomeCompleto, Date dataNascimento) {
        this(null, idPessoaFisica, cpf, nomeCompleto, dataNascimento, null, null, null, null, null, null, null, null,
                null, null, null);
    }

    @Override
    public String toString() {
        return getCpf() + " - " + getNomeCompleto();
    }
}
