package br.com.infox.epp.relatorio.produtividadeusuarios.view;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutividadeUsuariosLocalizacaoVO implements Comparable<ProdutividadeUsuariosLocalizacaoVO> {

	private String localizacao;
	private List<ProdutividadeUsuariosAssuntoQuantidadeVO> listaAssuntoQtdVO = new ArrayList<ProdutividadeUsuariosAssuntoQuantidadeVO>();
	
	@Override
	public int compareTo(ProdutividadeUsuariosLocalizacaoVO o) {
		return this.localizacao.compareTo(o.getLocalizacao());
	}
}
