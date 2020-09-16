package br.com.infox.epp.loglab.vo;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of="idPessoa")
public class PesquisaRequerenteVO {

    private Integer idPessoa;
    private String cpf;
    private String cnpj;
    private String nomeCompleto;

}
