package br.com.infox.epp.relatorio.quantitativoprocessos.analitico;

import java.util.Date;

import br.com.infox.core.util.DateUtil;
import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.relatorio.quantitativoprocessos.StatusProcessoEnum;
import br.com.infox.jsf.converter.CpfConverter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class RelatorioProcessosAnaliticoExcelVO {

    private String secretaria;
    private String assunto;
    private String numeroProcesso;
    private String usuarioSolicitante;
    private StatusProcessoEnum status;
    private String dataAbertura;
    private String nomeParticipante;
    private String tipoParticipante;
    private String cpfCnpj;
    private String tarefa;
    private String setor;
    private String dataInicio;
    private String dataFechamento;

    public RelatorioProcessosAnaliticoExcelVO(String secretaria, String assunto, String numeroProcesso,
            String usuarioSolicitante, Date dataFim, Date dataAbertura
            , String nomeParticipante
            , TipoPessoaEnum tipoPessoa
            , String cpfCnpj
            , String tipoParticipante
            , String tarefa
            , String setor
            , Date dataInicio
            , Date dataFechamento
    ){
        super();
        this.secretaria = secretaria;
        this.assunto = assunto;
        this.numeroProcesso = numeroProcesso;
        this.usuarioSolicitante = usuarioSolicitante;
        this.status = dataFim == null ? StatusProcessoEnum.A : StatusProcessoEnum.F;
        this.dataAbertura = DateUtil.formatarData(dataAbertura);

        this.nomeParticipante = nomeParticipante;
        this.tipoParticipante = tipoParticipante;

        if(!StringUtil.isEmpty(cpfCnpj)) {
            if(TipoPessoaEnum.F.equals(tipoPessoa)) {
                this.cpfCnpj = CpfConverter.format(cpfCnpj);
            } else if(TipoPessoaEnum.J.equals(tipoPessoa)) {
                this.cpfCnpj = CpfConverter.format(cpfCnpj);
            }
        }

        this.tarefa = tarefa;
        this.setor = setor;
        this.dataInicio = dataInicio != null ? DateUtil.formatarData(dataInicio, "dd/MM/yyyy HH:mm") : "";
        this.dataFechamento = dataFechamento != null ? DateUtil.formatarData(dataFechamento, "dd/MM/yyyy HH:mm") : "";
    }

}
