package br.com.infox.epp.loglab.dto;

import java.io.Serializable;

import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteProcessoLogLabDTO implements Serializable {

    private String cpf;
    private String cnpj;
    private String nome;
    private String nomeFantasia;
    private String razaoSocial;
    private String matricula;
    private String orgaoLotado;
    private String cargo;
    private String funcao;
    private TipoPessoaEnum tipoPessoa;

}
