package br.com.infox.epp.relatorio.quantitativoprocessos;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import br.com.infox.core.exception.ExcelExportException;
import br.com.infox.core.util.DateUtil;
import br.com.infox.core.util.ExcelExportUtil;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.jsf.util.JsfUtil;
import br.com.infox.seam.exception.BusinessRollbackException;
import br.com.infox.seam.path.PathResolver;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class RelatorioProcessosView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
    private PathResolver pathResolver;
	@Inject
	private FluxoDAO fluxoDAO;

	@Inject
	private RelatorioProcessosViewSearch relatorioProcessosViewSearch;

	@Getter @Setter
	private List<SelectItem> listaAssunto;
	@Getter @Setter
	private List<StatusProcessoEnum> listaStatus;

	@Getter @Setter
	@Size(min = 1)
	@NotNull
	private List<Integer> listaAssuntoSelecionado;
	@Getter @Setter
	private List<String> listaStatusSelecionado;
	@Getter @Setter
	private Date dataInicio;
	@Getter @Setter
	private Date dataFim;
	@Getter @Setter
	private Date dataMovimentacaoInicio;
	@Getter @Setter
	private Date dataMovimentacaoFim;
	@Getter @Setter
	private Date dataArquivamentoInicio;
	@Getter @Setter
	private Date dataArquivamentoFim;
	@Getter
    private boolean arquivadoSelecionado;


	@PostConstruct
	private void init() {
		listaStatus = Arrays.asList(StatusProcessoEnum.values());
		listaAssunto = fluxoDAO.getFluxosPrimariosAtivos().stream()
	        .map(f -> new SelectItem(f.getIdFluxo(), f.getFluxo()))
	        .collect(Collectors.toList());
	}

	@ExceptionHandled
	public void prepararAbrirRelatorioSintetico() {
	    JsfUtil jsfUtil = JsfUtil.instance();
        jsfUtil.addFlashParam("assuntos", listaAssuntoSelecionado);
        jsfUtil.addFlashParam("status", listaStatusSelecionado.stream()
            .map(o -> StatusProcessoEnum.valueOf(o))
            .collect(Collectors.toList())
        );
        jsfUtil.addFlashParam("dataAberturaInicio", dataInicio);
        jsfUtil.addFlashParam("dataAberturaFim", DateUtil.getEndOfDay(dataFim));
        jsfUtil.applyLastPhaseFlashAction();
	}

	@ExceptionHandled
	public void onChangeListaStatus() {
	    this.arquivadoSelecionado = getListaStatusSelecionado() != null && getListaStatusSelecionado().contains(StatusProcessoEnum.F.name());
	}

	@ExceptionHandled
	public void prepararAbrirRelatorioAnalitico() {
	    JsfUtil jsfUtil = JsfUtil.instance();
	    jsfUtil.addFlashParam("assuntos", listaAssuntoSelecionado);
	    jsfUtil.addFlashParam("status", listaStatusSelecionado.stream()
	            .map(o -> StatusProcessoEnum.valueOf(o))
	            .collect(Collectors.toList())
	            );
	    jsfUtil.addFlashParam("dataAberturaInicio", dataInicio);
	    jsfUtil.addFlashParam("dataAberturaFim", DateUtil.getEndOfDay(dataFim));
	    jsfUtil.addFlashParam("dataMovimentacaoInicio", dataMovimentacaoInicio);
	    jsfUtil.addFlashParam("dataMovimentacaoFim", DateUtil.getEndOfDay(dataMovimentacaoFim));
	    jsfUtil.addFlashParam("dataArquivamentoInicio", dataArquivamentoInicio);
	    jsfUtil.addFlashParam("dataArquivamentoFim", DateUtil.getEndOfDay(dataArquivamentoFim));
	    jsfUtil.applyLastPhaseFlashAction();
	}

	@ExceptionHandled
	public void gerarExcelSintetico() {
		try {
			String urlTemplate = pathResolver.getContextRealPath() + "/RelatorioQuantitativoProcessos/sinteticoReport.xls";
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("rowVO", relatorioProcessosViewSearch.getRelatorioSintetico(
                listaAssuntoSelecionado,
                dataInicio,
                DateUtil.getEndOfDay(dataFim),
                this.listaStatusSelecionado.stream()
	                .map(o -> StatusProcessoEnum.valueOf(o))
	                .collect(Collectors.toList())
            ));
	        ExcelExportUtil.downloadXLS(urlTemplate, map, "sinteticoReport.xls");
		} catch (ExcelExportException e) {
			throw new BusinessRollbackException("Erro inesperado", e);
		}
	}

	@ExceptionHandled
	public void prepararExcelAnalitico() {
	    JsfUtil jsfUtil = JsfUtil.instance();
        jsfUtil.addFlashParam("rowVO", relatorioProcessosViewSearch.getRelatorioAnalitico(
            listaAssuntoSelecionado
            ,dataInicio
            ,DateUtil.getEndOfDay(dataFim)
            ,dataMovimentacaoInicio
            ,DateUtil.getEndOfDay(dataMovimentacaoFim)
            ,dataArquivamentoInicio
            ,DateUtil.getEndOfDay(dataArquivamentoFim)
            ,this.listaStatusSelecionado.stream()
            .map(o -> StatusProcessoEnum.valueOf(o))
            .collect(Collectors.toList())
        ));
        jsfUtil.applyLastPhaseFlashAction();
	}

	@ExceptionHandled
	public void gerarExcelAnalitico() {
	    try {
	        Map<String, Object> map = new HashMap<String, Object>();
	        JsfUtil jsfUtil = JsfUtil.instance();
	        map.put("rowVO", jsfUtil.getFlashParam("rowVO", List.class));
	        String urlTemplate = pathResolver.getContextRealPath() + "/RelatorioQuantitativoProcessos/analiticoReport.xls";
	        ExcelExportUtil.downloadXLS(urlTemplate, map, "analiticoReport.xls");
	    } catch (ExcelExportException e) {
	        throw new BusinessRollbackException("Erro inesperado", e);
	    }
	}

}
