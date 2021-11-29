package br.com.infox.epp.relatorio.quantitativoprocessos.sitetico;

import java.util.Date;

import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.relatorio.quantitativoprocessos.StatusProcessoEnum;
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
    private Integer idProcesso;
    private String numeroProcesso;
    private String usuarioSolicitante;
    private StatusProcessoEnum status;
    private String dataAbertura;
    private String descricaoTarefa;

    public RelatorioProcessosSinteticoExcelVO(String secretaria, String assunto, Integer idProcesso, String numeroProcesso,
            String usuarioSolicitante, Date dataEncerramento, Date dataAbertura) {
        super();
        this.secretaria = secretaria;
        this.assunto = assunto;
        this.idProcesso = idProcesso;
        this.numeroProcesso = numeroProcesso;
        this.usuarioSolicitante = usuarioSolicitante;
        this.status = dataEncerramento == null? StatusProcessoEnum.A : StatusProcessoEnum.F;
        this.dataAbertura = DateUtil.formatarData(dataAbertura);
    }

}
