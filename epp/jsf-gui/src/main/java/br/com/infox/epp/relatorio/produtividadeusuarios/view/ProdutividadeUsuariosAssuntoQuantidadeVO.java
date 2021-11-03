package br.com.infox.epp.relatorio.produtividadeusuarios.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutividadeUsuariosAssuntoQuantidadeVO {
	
	private String assunto;
	private long qtdIniciada;
	private long qtdEmAndamento;
	private long qtdArquivadas;

}
