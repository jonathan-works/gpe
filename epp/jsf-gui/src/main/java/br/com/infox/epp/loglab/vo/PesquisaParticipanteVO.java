package br.com.infox.epp.loglab.vo;

import br.com.infox.epp.loglab.contribuinte.type.TipoParticipanteEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PesquisaParticipanteVO {
    
    private TipoParticipanteEnum tipoParticipante;
    private String cpf;
    private String cnpj;
    private String matricula;
    private String nomeCompleto;
    private String razaoSocial;
    private String nomeFantasia;
}
