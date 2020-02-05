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
    private Integer idParticipantePai;
    private String cdTipoParte;
    private Date dataInicio;
    private Date dataFim;
    private EmpresaVO empresaVO;
    private ServidorContribuinteVO servidorContribuinteVO;

    public Integer getIdPessoa() {
        if(tipoPessoa.equals(TipoPessoaEnum.F)) {
            return servidorContribuinteVO != null ? servidorContribuinteVO.getIdPessoaFisica() : null;
        } else {
            return empresaVO != null ? empresaVO.getIdPessoaJuridica() : null;
        }
    }

    public String getCodigoPessoa() {
        if(tipoPessoa.equals(TipoPessoaEnum.F)) {
            return servidorContribuinteVO != null ? servidorContribuinteVO.getCpf() : null;
        } else {
            return empresaVO != null ? empresaVO.getCnpj() : null;
        }
    }

    public String getNome() {
        if(tipoPessoa.equals(TipoPessoaEnum.F)) {
            return servidorContribuinteVO != null ? servidorContribuinteVO.getNomeCompleto() : null;
        } else {
            return empresaVO != null ? empresaVO.getNomeFantasia() : null;
        }
    }

}
