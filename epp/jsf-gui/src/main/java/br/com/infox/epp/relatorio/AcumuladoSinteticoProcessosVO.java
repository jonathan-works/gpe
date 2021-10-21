package br.com.infox.epp.relatorio;

import java.util.Date;

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
	private String localizacao;
	private Date abertura;
	private Date encerramento;
}
