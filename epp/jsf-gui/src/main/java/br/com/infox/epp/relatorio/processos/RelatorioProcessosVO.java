package br.com.infox.epp.relatorio.processos;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RelatorioProcessosVO {

	private String numeroProcesso;
	private String usuarioSolicitante;
	private String statusProcesso;
	private String assunto;
	private Date dataAbertura;

}
