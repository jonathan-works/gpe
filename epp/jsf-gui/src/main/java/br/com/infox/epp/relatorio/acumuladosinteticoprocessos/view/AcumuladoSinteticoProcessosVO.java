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
public class AcumuladoSinteticoProcessosVO {

	private String numeroProcesso;
	private String fluxo;
	private String usuarioAbertura;
	private Date abertura;
	private Date encerramento;
	
}
