package br.com.infox.epp.loglab.vo;

import java.util.Date;

import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.seam.exception.BusinessRollbackException;
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
    private AnonimoVO anonimoVO;


    public Integer getIdPessoa() {
        if(TipoPessoaEnum.F.equals(tipoPessoa)) {
            return servidorContribuinteVO != null ? servidorContribuinteVO.getIdPessoaFisica() : null;
        } else if(TipoPessoaEnum.J.equals(tipoPessoa)) {
            return empresaVO != null ? empresaVO.getIdPessoaJuridica() : null;
        } else if(TipoPessoaEnum.A.equals(tipoPessoa)) {
            return anonimoVO != null ? anonimoVO.getIdPessoa() : null;
        }
        throw new BusinessRollbackException("Tipo pessoa não identificado.");
    }

    public String getCodigoPessoa() {
        if(TipoPessoaEnum.F.equals(tipoPessoa)) {
            return servidorContribuinteVO != null ? servidorContribuinteVO.getCpf() : null;
        } else if(TipoPessoaEnum.J.equals(tipoPessoa)) {
            return empresaVO != null ? empresaVO.getCnpj() : null;
        } else {
            return null;
        }
    }

    public String getNome() {
        if(TipoPessoaEnum.F.equals(tipoPessoa)) {
            return servidorContribuinteVO != null ? servidorContribuinteVO.getNomeCompleto() : null;
        } else if(TipoPessoaEnum.J.equals(tipoPessoa)) {
            return empresaVO != null ? empresaVO.getNomeFantasia() : null;
        } else if(TipoPessoaEnum.A.equals(tipoPessoa)) {
            return anonimoVO != null ? anonimoVO.getNomeDefaultIfNull() : null;
        }
        throw new BusinessRollbackException("Tipo pessoa não identificado.");
    }

}
