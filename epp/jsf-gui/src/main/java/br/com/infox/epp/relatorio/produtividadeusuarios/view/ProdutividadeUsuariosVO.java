package br.com.infox.epp.relatorio.produtividadeusuarios.view;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProdutividadeUsuariosVO {
	
	private String usuario;
	private List<ProdutividadeUsuariosLocalizacaoVO> listaLocalizacaoVO = new ArrayList<ProdutividadeUsuariosLocalizacaoVO>();

}
