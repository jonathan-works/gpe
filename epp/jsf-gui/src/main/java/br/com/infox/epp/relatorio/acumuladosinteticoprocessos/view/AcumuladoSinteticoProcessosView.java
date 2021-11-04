package br.com.infox.epp.relatorio.acumuladosinteticoprocessos.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.exception.ExcelExportException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.core.util.ExcelExportUtil;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.relatorio.acumuladosinteticoprocessos.AcumuladoSinteticoProcessosSearch;
import br.com.infox.seam.path.PathResolver;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class AcumuladoSinteticoProcessosView implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Inject
    private PathResolver pathResolver;
	@Inject
	private FluxoDAO fluxoDAO;
	@Inject
	private AcumuladoSinteticoProcessosSearch acumuladoSinteticoProcessosSearch;
	
	@Getter @Setter
	private List<AcumuladoSinteticoProcessosVO> listaRelatorioExcel;
	
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
		
		listaMesSelecionado = new ArrayList<String>();
	}
	
	public void marcarTodos() {
		listaMesSelecionado.addAll(listaMes);
	}
	
	public void desmarcarTodos() {
		listaMesSelecionado.clear();
	}
	
	public void prepararAbrirRelatorio() {
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put("listaAssuntoAcumuladoSinteticoProcessosView", listaAssuntoSelecionado);
		sessionMap.put("listaStatusAcumuladoSinteticoProcessosView", listaStatusSelecionado);
		sessionMap.put("listaMesAcumuladoSinteticoProcessosView", listaMesSelecionado);
		sessionMap.put("anoAcumuladoSinteticoProcessosView", ano);
	}
	
	public void gerarExcel() {
		try {
			gerarRelatorio();
			String urlTemplate = pathResolver.getContextRealPath() + "/RelatorioAcumuladoProcessos/reportAcumuladoSinteticoProcessos.xls";
	        Map<String, Object> map = new HashMap<String, Object>();
	        StringBuilder className = new StringBuilder("acumuladoSinteticoProcessosVO");
	        map.put(className.toString(), listaRelatorioExcel);
	        ExcelExportUtil.downloadXLS(urlTemplate, map, "reportAcumuladoSinteticoProcessos.xls");
		} catch (ExcelExportException e) {
			e.printStackTrace();
		}
	}
	
	private void gerarRelatorio() {
		listaRelatorioExcel = new ArrayList<AcumuladoSinteticoProcessosVO>();
		if(listaStatusSelecionado.isEmpty() || listaStatusSelecionado.contains("Em andamento")) {
			listaRelatorioExcel.addAll(acumuladoSinteticoProcessosSearch.gerarRelatorio(listaAssuntoSelecionado, "Em andamento", listaMesSelecionado, ano, Authenticator.getLocalizacaoAtual()));
		}
		
		if(listaStatusSelecionado.isEmpty() || listaStatusSelecionado.contains("Arquivados/Finalizados")) {
			listaRelatorioExcel.addAll(acumuladoSinteticoProcessosSearch.gerarRelatorio(listaAssuntoSelecionado, "Arquivados/Finalizados", listaMesSelecionado, ano, Authenticator.getLocalizacaoAtual()));
		}
	}

}
