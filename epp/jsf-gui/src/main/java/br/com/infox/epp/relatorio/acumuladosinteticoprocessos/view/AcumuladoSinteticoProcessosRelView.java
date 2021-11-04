package br.com.infox.epp.relatorio.acumuladosinteticoprocessos.view;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.relatorio.acumuladosinteticoprocessos.AcumuladoSinteticoProcessosSearch;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class AcumuladoSinteticoProcessosRelView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private AcumuladoSinteticoProcessosSearch acumuladoSinteticoProcessosSearch;
	
	@Getter @Setter
	private List<AcumuladoSinteticoProcessosLocalizacaoVO> listaRelatorio;
	
	@Getter @Setter
	private List<Fluxo> listaAssuntoSelecionado;
	@Getter @Setter
	private List<String> listaStatusSelecionado;
	@Getter @Setter
	private List<String> listaMesSelecionado;
	@Getter @Setter
	private Integer ano;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		listaAssuntoSelecionado = (List<Fluxo>) sessionMap.get("listaAssuntoAcumuladoSinteticoProcessosView");
		listaStatusSelecionado = (List<String>) sessionMap.get("listaStatusAcumuladoSinteticoProcessosView");
		listaMesSelecionado = (List<String>) sessionMap.get("listaMesAcumuladoSinteticoProcessosView");
		ano = (Integer) sessionMap.get("anoAcumuladoSinteticoProcessosView");
		
		sessionMap.remove("listaAssuntoAcumuladoSinteticoProcessosView");
		sessionMap.remove("listaStatusAcumuladoSinteticoProcessosView");
		sessionMap.remove("listaMesAcumuladoSinteticoProcessosView");
		sessionMap.remove("anoAcumuladoSinteticoProcessosView");
		
		gerarRelatorio();
	}
	
	public String getMesesSelecionadosPorExtenso() {
		return String.join(", ", listaMesSelecionado);
	}
	
	private void gerarRelatorio() {
		listaRelatorio = acumuladoSinteticoProcessosSearch.gerarRelatorio(listaAssuntoSelecionado, listaStatusSelecionado, listaMesSelecionado, ano, Authenticator.getLocalizacaoAtual());
	}
}
