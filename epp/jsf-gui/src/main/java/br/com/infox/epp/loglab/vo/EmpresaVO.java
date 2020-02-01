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
    private String cidade;
    private String logradouro;
    private String bairro;
    private String complemento;
    private String numero;
    private String cep;

    @Override
    public String toString() {
        return getCnpj() + " - " + getNomeFantasia();
    }
}
