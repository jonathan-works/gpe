package br.com.infox.epp.loglab.vo;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

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
public class EmpresaVO {

    private Long id;
    private Integer idPessoaJuridica;
    @NotNull
    private String cnpj;
    private String tipoEmpresa;
    @NotNull
    @Size(min = 6, max = 256)
    private String razaoSocial;
    @NotNull
    @Size(min = 6, max = 256)
    private String nomeFantasia;
    @NotNull
    private Date dataAbertura;
    @NotNull
    private String telefoneCelular;
    private String telefoneFixo;
    @NotNull
    private String email;
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
