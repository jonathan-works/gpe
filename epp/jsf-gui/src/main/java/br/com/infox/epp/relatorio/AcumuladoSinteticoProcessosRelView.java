package br.com.infox.epp.relatorio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.relatorio.search.AcumuladoSinteticoProcessosSearch;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class AcumuladoSinteticoProcessosRelView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private AcumuladoSinteticoProcessosSearch acumuladoSinteticoProcessosSearch;
	
	@Getter @Setter
	private List<AcumuladoSinteticoProcessosVO> listaRelatorioEmAndamento;
	@Getter @Setter
	private List<AcumuladoSinteticoProcessosVO> listaRelatorioFinalizadoArquivado;
	
	@Getter @Setter
	private List<Fluxo> listaAssuntoSelecionado;
	@Getter @Setter
	private List<String> listaStatusSelecionado;
	@Getter @Setter
	private List<String> listaMesSelecionado;
	@Getter @Setter
	private Integer ano;
	@Getter @Setter
	private Localizacao localizacao;
	
	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() {
		localizacao = Authenticator.getLocalizacaoAtual();
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
		initRelatorios();
		if(listaStatusSelecionado.isEmpty() || listaStatusSelecionado.contains("Em andamento")) {
			listaRelatorioEmAndamento = acumuladoSinteticoProcessosSearch.gerarRelatorio(listaAssuntoSelecionado, "Em andamento", listaMesSelecionado, ano, localizacao);
		}
		
		if(listaStatusSelecionado.isEmpty() || listaStatusSelecionado.contains("Arquivados/Finalizados")) {
			listaRelatorioFinalizadoArquivado = acumuladoSinteticoProcessosSearch.gerarRelatorio(listaAssuntoSelecionado, "Arquivados/Finalizados", listaMesSelecionado, ano, localizacao);
		}
	}
	
	private void initRelatorios() {
		listaRelatorioEmAndamento = new ArrayList<AcumuladoSinteticoProcessosVO>();
		listaRelatorioFinalizadoArquivado = new ArrayList<AcumuladoSinteticoProcessosVO>();
	}

}
