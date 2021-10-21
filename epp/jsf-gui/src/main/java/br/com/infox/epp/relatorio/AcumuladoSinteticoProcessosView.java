package br.com.infox.epp.relatorio;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.relatorio.search.AcumuladoSinteticoProcessosSearch;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class AcumuladoSinteticoProcessosView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private FluxoDAO fluxoDAO;
	@Inject
	AcumuladoSinteticoProcessosSearch acumuladoSinteticoProcessosSearch;
	
	@Getter @Setter
	private List<Fluxo> listaAssunto;
	@Getter @Setter
	private List<String> listaStatus;
	@Getter @Setter
	private List<String> listaMes;
	
	@Getter @Setter
	private List<Fluxo> listaAssuntoSelecionado;
	@Getter @Setter
	private List<String> listaStatusSelecionado;
	@Getter @Setter
	private List<String> listaMesSelecionado;
	@Getter @Setter
	private Integer ano;
	
	
	@PostConstruct
	private void init() {
		listaAssunto = fluxoDAO.getFluxosPrimariosAtivos();
		listaStatus = Arrays.asList("Em andamento", "Arquivados/Finalizados");
		listaMes = DateUtil.getListaTodosMeses();
	}
	
	public void gerarRelatorio() {
		if(listaStatusSelecionado.isEmpty() || listaStatusSelecionado.contains("Em andamento")) {
			acumuladoSinteticoProcessosSearch.gerarRelatorio(listaAssuntoSelecionado, "Em andamento", listaMesSelecionado, ano);
		}
		
		if(listaStatusSelecionado.isEmpty() || listaStatusSelecionado.contains("Arquivados/Finalizados")) {
			acumuladoSinteticoProcessosSearch.gerarRelatorio(listaAssuntoSelecionado, "Arquivados/Finalizados", listaMesSelecionado, ano);
		}
	}

}
