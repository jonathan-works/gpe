package br.com.infox.epp.loglab.vo;

import java.util.Date;

import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipanteProcessoVO {

    private TipoPessoaEnum tipoPessoa = TipoPessoaEnum.F;
    private Integer idProcesso;
    private String cdTipoParte;
    private Date dataInicio;
    private Date dataFim;
    private EmpresaVO empresaVO;
    private ServidorContribuinteVO servidorContribuinteVO;

}
