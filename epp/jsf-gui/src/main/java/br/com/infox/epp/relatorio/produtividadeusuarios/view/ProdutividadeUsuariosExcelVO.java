package br.com.infox.epp.relatorio.produtividadeusuarios.view;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class ProdutividadeUsuariosExcelVO {

	private String usuario;
	private String setor;
	private String assunto;
	private long qtdIniciadas;
	private long qtdEmAndamento;
	private long qtdArquivadas;
}
