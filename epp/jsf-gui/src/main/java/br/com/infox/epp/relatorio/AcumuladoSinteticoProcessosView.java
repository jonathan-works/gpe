package br.com.infox.epp.relatorio;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.util.DateUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class AcumuladoSinteticoProcessosView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
	private FluxoDAO fluxoDAO;
	
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
	
	public void prepararAbrirRelatorio() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put("listaAssuntoAcumuladoSinteticoProcessosView", listaAssuntoSelecionado);
		sessionMap.put("listaStatusAcumuladoSinteticoProcessosView", listaStatusSelecionado);
		sessionMap.put("listaMesAcumuladoSinteticoProcessosView", listaMesSelecionado);
		sessionMap.put("anoAcumuladoSinteticoProcessosView", ano);
	}

}
