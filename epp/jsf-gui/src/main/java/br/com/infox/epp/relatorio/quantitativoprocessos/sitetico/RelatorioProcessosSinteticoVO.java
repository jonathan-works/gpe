package br.com.infox.epp.relatorio.quantitativoprocessos.sitetico;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infox.epp.relatorio.quantitativoprocessos.StatusProcessoEnum;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "localizacao")
public class RelatorioProcessosSinteticoVO {

    private String localizacao;
    private List<RelatorioProcessosSinteticoFluxoVO> lista = new ArrayList<>();

    public RelatorioProcessosSinteticoVO(String localizacao) {
        super();
        this.localizacao = localizacao;
    }

    @Getter @Setter
    @NoArgsConstructor
    @EqualsAndHashCode(of = "fluxo")
    public static class RelatorioProcessosSinteticoFluxoVO{
        private String fluxo;
        private List<RelatorioProcessosSinteticoRowVO> lista = new ArrayList<>();
        public RelatorioProcessosSinteticoFluxoVO(String fluxo) {
            super();
            this.fluxo = fluxo;
        }
    }

    @Getter @Setter
    @NoArgsConstructor
    @EqualsAndHashCode(of = "numeroProcesso")
    public static class RelatorioProcessosSinteticoRowVO{
        private Integer idProcesso;
        private String numeroProcesso;
        private String usuarioSolicitante;
        private StatusProcessoEnum status;
        private Date dataAbertura;
        private String descricaoTarefa;
        public RelatorioProcessosSinteticoRowVO(Integer idProcesso, String numeroProcesso, String usuarioSolicitante,
                Date dataEncerramento, Date dataAbertura) {
            super();
            this.idProcesso = idProcesso;
            this.numeroProcesso = numeroProcesso;
            this.usuarioSolicitante = usuarioSolicitante;
            this.status = dataEncerramento == null ? StatusProcessoEnum.A : StatusProcessoEnum.F;
            this.dataAbertura = dataAbertura;
        }

    }
}

