package br.com.infox.epp.loglab.vo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.epp.pessoa.annotation.Cpf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SolicitacaoCadastroVO {

    @NotNull
    @Size(min = 6, max = 256)
    private String nomeCompleto;

    @NotNull
    @Cpf
    private String cpf;

    @NotNull
    @Size(min = 3, max = 20)
    private String numeroRg;

    @NotNull
    @Size(min = 3, max = 256)
    private String emissorRg;

    @NotNull
    private String ufRg;

    @NotNull
    private String telefone;

    private String cargo;

    private String departamento;

    @Override
    public String toString() {
        return nomeCompleto;
    }
}
