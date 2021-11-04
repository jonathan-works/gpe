package br.com.infox.epp.relatorio.acumuladosinteticoprocessos.view;

import java.util.Date;

import br.com.infox.core.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AcumuladoSinteticoProcessosExcelVO {

	private String localizacao;
	private String numeroProcesso;
	private String fluxo;
	private String usuarioAbertura;
	private Date abertura;
	private Date encerramento;
	
	public String getMes() {
		return encerramento == null? DateUtil.formatarData(abertura, "MM") : DateUtil.formatarData(encerramento, "MM");
	}
	
	public String getAberturaExtenso() {
		return DateUtil.formatarData(abertura);
	}
	
	public String getEncerramentoExtenso() {
		return encerramento != null ? DateUtil.formatarData(encerramento) : "";
	}
	
	public String getStatus() {
		return encerramento == null? "Em andamento" : "Arquivados";
	}
}
