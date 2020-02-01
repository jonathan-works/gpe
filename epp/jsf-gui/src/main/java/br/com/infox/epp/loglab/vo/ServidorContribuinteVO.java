package br.com.infox.epp.loglab.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.loglab.contribuinte.type.TipoParticipanteEnum;
import br.com.infox.epp.loglab.model.ContribuinteSolicitante;
import br.com.infox.epp.loglab.model.Servidor;
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
    private Date dataNascimento;
    private String nomeMae;
    @NotNull
    private String email;
    private String celular;
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

    public ServidorContribuinteVO(ContribuinteSolicitante contribuinte) {
        this.id = contribuinte.getId();
        this.idPessoaFisica = contribuinte.getPessoaFisica().getIdPessoa();
        this.bairro = contribuinte.getBairro();
        this.cep = contribuinte.getCep();
        this.cidade = contribuinte.getCidade();
        this.complemento = contribuinte.getComplemento();
        this.cpf = contribuinte.getCpf();
        this.dataNascimento = contribuinte.getDataNascimento();
        this.email = contribuinte.getEmail();
        if(contribuinte.getEstado() != null) {
            this.codEstado = contribuinte.getEstado().getCodigo();
        }
        this.logradouro = contribuinte.getLogradouro();
        this.nomeCompleto = contribuinte.getNomeCompleto();
        this.nomeMae = contribuinte.getNomeMae();
        this.numero = contribuinte.getNumero();
        this.sexo = contribuinte.getSexo();
        this.celular = contribuinte.getTelefone();
        this.tipoParticipante = TipoParticipanteEnum.CO;
    }

    public ServidorContribuinteVO(Servidor servidor) {
        this.id = servidor.getId();
        this.cargoCarreira = servidor.getCargoCarreira();
        this.cargoComissao = servidor.getCargoComissao();
        this.celular = servidor.getCelular();
        this.cpf = servidor.getCpf();
        this.dataEmissaoRg = servidor.getDataEmissaoRg();
        this.dataExercicio = servidor.getDataExercicio();
        this.dataNascimento = servidor.getDataNascimento();
        this.dataNomeacao = servidor.getDataNomeacaoContratacao();
        this.dataPosse = servidor.getDataPosse();
        this.localTrabalho = servidor.getDepartamento();
        this.email = servidor.getEmail();
        this.jornada = servidor.getJornada();
        this.nomeMae = servidor.getMae();
        this.matricula = servidor.getMatricula();
        this.nomeCompleto = servidor.getNomeCompleto();
        this.numeroRg = servidor.getNumeroRg();
        this.ocupacaoCarreira = servidor.getOcupacaoCarreira();
        this.ocupacaoComissao = servidor.getOcupacaoComissao();
        this.orgaoEmissorRG = servidor.getOrgaoEmissorRG();
        this.nomePai = servidor.getPai();
        this.idPessoaFisica = servidor.getPessoaFisica().getIdPessoa();
        this.orgao = servidor.getSecretaria();
        this.situacao = servidor.getSituacao();
        this.subFolha = servidor.getSubFolha();
        this.tipoParticipante = TipoParticipanteEnum.SE;
    }

    @Override
    public String toString() {
        return getCpf() + " - " + getNomeCompleto();
    }
}
