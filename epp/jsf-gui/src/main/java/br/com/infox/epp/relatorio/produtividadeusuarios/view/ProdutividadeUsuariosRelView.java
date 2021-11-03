package br.com.infox.epp.relatorio.produtividadeusuarios.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.relatorio.produtividadeusuarios.ProdutividadeUsuariosSearch;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ProdutividadeUsuariosRelView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private ProdutividadeUsuariosSearch produtividadeUsuariosSearch;
	
	@Getter @Setter
	private List<UsuarioLogin> listaUsuarioSelecionado = new ArrayList<UsuarioLogin>();
	@Getter @Setter
	private List<Fluxo> listaAssuntoSelecionado = new ArrayList<Fluxo>();
	@Getter @Setter
	private Date dataInicial;
	@Getter @Setter
	private Date dataFinal;
	
	@Getter @Setter
	private List<ProdutividadeUsuariosVO> listaRelatorio;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		listaUsuarioSelecionado = (List<UsuarioLogin>) sessionMap.get("listaUsuarioProdutividadeUsuariosView");
		listaAssuntoSelecionado = (List<Fluxo>) sessionMap.get("listaAssuntoProdutividadeUsuariosView");
		dataInicial = (Date) sessionMap.get("dataInicialProdutividadeUsuariosView");
		dataFinal = (Date) sessionMap.get("dataFinalProdutividadeUsuariosView");
		
		sessionMap.remove("listaAssuntoProdutividadeUsuariosView");
		sessionMap.remove("listaUsuarioProdutividadeUsuariosView");
		sessionMap.remove("dataInicialProdutividadeUsuariosView");
		sessionMap.remove("dataFinalProdutividadeUsuariosView");
		
		gerarRelatorio();
	}
	
	private void gerarRelatorio() {
		listaRelatorio = produtividadeUsuariosSearch.gerarRelatorio(listaUsuarioSelecionado, listaAssuntoSelecionado, dataInicial, dataFinal);
	}

}
