package br.com.infox.epp.tarefaexterna.view;

import java.io.Serializable;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = "cpf")
public class CadastroTarefaExternaPessoaVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotBlank
    private String cpf;
    @NotBlank
    private String nome;
    @Pattern(regexp = "M|F", flags = Pattern.Flag.CASE_INSENSITIVE)
    @NotBlank
    private String sexo;
    @NotBlank
    private String cep;
    @NotBlank
    private String endereco;
    @NotBlank
    private String numero;
    @NotBlank
    private String bairro;
    private String complemento;
    @NotBlank
    private String uf;
    @NotBlank
    private String municipio;
    private String telefoneCelular;
    private String telefoneFixo;
    private String email;

}
