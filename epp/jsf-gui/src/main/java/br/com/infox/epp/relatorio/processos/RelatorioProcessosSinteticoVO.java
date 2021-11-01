package br.com.infox.epp.relatorio.processos;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        private String numeroProcesso;
        private String usuarioSolicitante;
        private StatusProcessoEnum status;
        private Date dataAbertura;
        public RelatorioProcessosSinteticoRowVO(String numeroProcesso, String usuarioSolicitante,
                Boolean emAndamento, Date dataAbertura) {
            super();
            this.numeroProcesso = numeroProcesso;
            this.usuarioSolicitante = usuarioSolicitante;
            this.status = emAndamento ? StatusProcessoEnum.A : StatusProcessoEnum.F;
            this.dataAbertura = dataAbertura;
        }


    }
}

