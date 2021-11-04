package br.com.infox.epp.relatorio.quantitativoprocessos.analitico;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infox.core.util.StringUtil;
import br.com.infox.epp.pessoa.type.TipoPessoaEnum;
import br.com.infox.epp.relatorio.quantitativoprocessos.StatusProcessoEnum;
import br.com.infox.jsf.converter.CpfConverter;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "localizacao")
public class RelatorioProcessosAnaliticoVO {

    private String localizacao;
    private List<RelatorioProcessosAnaliticoFluxoVO> lista = new ArrayList<>();

    public RelatorioProcessosAnaliticoVO(String localizacao) {
        super();
        this.localizacao = localizacao;
    }

    @Getter @Setter
    @NoArgsConstructor
    @EqualsAndHashCode(of = "fluxo")
    public static class RelatorioProcessosAnaliticoFluxoVO{
        private String fluxo;
        private List<RelatorioProcessosAnaliticoRowVO> lista = new ArrayList<>();
        public RelatorioProcessosAnaliticoFluxoVO(String fluxo) {
            super();
            this.fluxo = fluxo;
        }
    }

    @Getter @Setter
    @NoArgsConstructor
    @EqualsAndHashCode(of = "cpfCnpj")
    public static class RelatorioProcessosAnaliticoParticipanteVO{
        private String nome;
        private String cpfCnpj;
        private String tipoParticipante;
        public RelatorioProcessosAnaliticoParticipanteVO(
            String nome,
            String cpfCnpj,
            TipoPessoaEnum tipoPessoa,
            String tipoParticipante
        ){
            super();
            this.nome = nome;
            if(!StringUtil.isEmpty(cpfCnpj)) {
                if(TipoPessoaEnum.F.equals(tipoPessoa)) {
                    this.cpfCnpj = CpfConverter.format(cpfCnpj);
                } else if(TipoPessoaEnum.J.equals(tipoPessoa)) {
                    this.cpfCnpj = CpfConverter.format(cpfCnpj);
                }
            }
            this.tipoParticipante = tipoParticipante;
        }
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatorioProcessosAnaliticoTarefaVO {
        private String nome;
        private String setor;
        private Date dataInicio;
        private Date dataFim;
    }

    @Getter @Setter
    @NoArgsConstructor
    @EqualsAndHashCode(of = "numeroProcesso")
    public static class RelatorioProcessosAnaliticoRowVO{
        private Integer idProcesso;
        private String numeroProcesso;
        private String usuarioSolicitante;
        private Date dataAbertura;
        private StatusProcessoEnum status;
        private List<RelatorioProcessosAnaliticoParticipanteVO> participantes;
        private List<RelatorioProcessosAnaliticoTarefaVO> tarefas;
        public RelatorioProcessosAnaliticoRowVO(
            Integer idProcesso,
            String numeroProcesso,
            String usuarioSolicitante,
            Date dataFim,
            Date dataAbertura
        ){
            super();
            this.idProcesso = idProcesso;
            this.numeroProcesso = numeroProcesso;
            this.usuarioSolicitante = usuarioSolicitante;
            this.status = dataFim == null ? StatusProcessoEnum.A : StatusProcessoEnum.F;
            this.dataAbertura = dataAbertura;
        }
    }
}

