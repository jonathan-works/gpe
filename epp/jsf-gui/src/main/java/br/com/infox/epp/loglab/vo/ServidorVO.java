package br.com.infox.epp.loglab.vo;

import java.util.Date;

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

    @Size(min = 6, max = 256)
    private String nomeCompleto;

    @Size(min = 3, max = 256)
    private String cargoFuncao;

    private String telefone;

    @NotNull
    private String email;
    //Orgao
    private String secretaria;
    //LocalTrabalho
    private String departamento;
    private Date dataNomeacaoContratacao;
    private Date dataPosse;
    private Date dataExercicio;
    private String situacao;
    private String SubFolha;
    private String jornada;
    private String ocupacaoCarreira;
    private String cargoCarreira;
    private String ocupacaoComissao;
    private String cargoComissao;
    private String pai;
    private String mae;
    private Date dataNascimento;
    private String numeroRg;
    private Date dataEmissaoRg;
    private String orgaoEmissoRG;

}
