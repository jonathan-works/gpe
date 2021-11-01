package br.com.infox.epp.relatorio.processos;

import java.util.Date;

import br.com.infox.core.util.DateUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"numeroProcesso", "assunto", "secretaria"})
public class RelatorioProcessosSinteticoExcelVO {

    private String secretaria;
    private String assunto;
    private String numeroProcesso;
    private String usuarioSolicitante;
    private StatusProcessoEnum status;
    private String dataAbertura;

    public RelatorioProcessosSinteticoExcelVO(String secretaria, String assunto, String numeroProcesso,
            String usuarioSolicitante, Boolean emAndamento, Date dataAbertura) {
        super();
        this.secretaria = secretaria;
        this.assunto = assunto;
        this.numeroProcesso = numeroProcesso;
        this.usuarioSolicitante = usuarioSolicitante;
        this.status = emAndamento ? StatusProcessoEnum.A : StatusProcessoEnum.F;
        this.dataAbertura = DateUtil.formatarData(dataAbertura);
    }

}
