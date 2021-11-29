package br.com.infox.epp.relatorio.produtividadeusuarios.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.primefaces.context.RequestContext;

import br.com.infox.core.exception.ExcelExportException;
import br.com.infox.core.util.ExcelExportUtil;
import br.com.infox.core.util.RelatorioUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.dao.FluxoDAO;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.relatorio.produtividadeusuarios.ProdutividadeUsuariosSearch;
import br.com.infox.epp.system.Parametros;
import br.com.infox.seam.exception.BusinessException;
import br.com.infox.seam.path.PathResolver;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class ProdutividadeUsuariosView implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
    private PathResolver pathResolver;
	@Inject
	private FluxoDAO fluxoDAO;
	@Inject
	private ProdutividadeUsuariosSearch produtividadeUsuariosSearch;

	@Getter @Setter
	private List<UsuarioLogin> listaUsuario;
	@Getter @Setter
	private List<Fluxo> listaAssunto;
	@Getter @Setter
	private Date dataInicial;
	@Getter @Setter
	private Date dataFinal;

	@Getter @Setter
	private List<UsuarioLogin> listaUsuarioSelecionado;
	@Getter @Setter
	private List<Fluxo> listaAssuntoSelecionado;

	private List<ProdutividadeUsuariosExcelVO> listaRelatorioExcel;

	@PostConstruct
	private void init() {
		listaAssunto = fluxoDAO.getFluxosAtivosList();
		listaUsuario = produtividadeUsuariosSearch.getUsuariosLocalizacaoAbaixoHierarquia();
	}

	public void prepararAbrirRelatorio() {
		validarDatas();
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		sessionMap.put("listaAssuntoProdutividadeUsuariosView", listaAssuntoSelecionado);
		sessionMap.put("listaUsuarioProdutividadeUsuariosView", listaUsuarioSelecionado);
		sessionMap.put("dataInicialProdutividadeUsuariosView", dataInicial);
		sessionMap.put("dataFinalProdutividadeUsuariosView", dataFinal);
		RequestContext.getCurrentInstance().execute("document.getElementById('relatorioForm:openPDF').click();");
	}

	public void preparaAbrirExcel() {
		validarDatas();
		RequestContext.getCurrentInstance().execute("document.getElementById('relatorioForm:gerarExcel').click();");
	}

	public void gerarExcel() {
		try {
			gerarRelatorio();
			String urlTemplate = pathResolver.getContextRealPath() + "/RelatorioProdutividadeUsuarios/reportProdutividadeUsuarios.xls";
	        Map<String, Object> map = new HashMap<String, Object>();
	        map.put("cabecalhoEmissao", RelatorioUtil.getDadosEmissao());
	        map.put("acumuladoSinteticoProcessosVO", listaRelatorioExcel);
	        ExcelExportUtil.downloadXLS(urlTemplate, map, "reportProdutividadeUsuarios.xls");
		} catch (ExcelExportException e) {
			e.printStackTrace();
		}
	}

	private void gerarRelatorio() {
		listaRelatorioExcel = new ArrayList<ProdutividadeUsuariosExcelVO>();
		for(ProdutividadeUsuariosVO usuarioVO : produtividadeUsuariosSearch.gerarRelatorio(listaUsuarioSelecionado, listaAssuntoSelecionado, dataInicial, dataFinal)) {
			for(ProdutividadeUsuariosLocalizacaoVO localizacaoVO : usuarioVO.getListaLocalizacaoVO()) {
				for(ProdutividadeUsuariosAssuntoQuantidadeVO assuntoVO : localizacaoVO.getListaAssuntoQtdVO()) {
					ProdutividadeUsuariosExcelVO vo = new ProdutividadeUsuariosExcelVO();
					vo.setUsuario(usuarioVO.getUsuario());
					vo.setSetor(localizacaoVO.getLocalizacao());
					vo.setAssunto(assuntoVO.getAssunto());
					vo.setQtdIniciadas(assuntoVO.getQtdIniciada());
					vo.setQtdEmAndamento(assuntoVO.getQtdEmAndamento());
					vo.setQtdArquivadas(assuntoVO.getQtdArquivadas());
					listaRelatorioExcel.add(vo);
				}
			}
		}
	}

	private void validarDatas() {
		String erro = "";
		if(dataFinal == null) {
			dataFinal = new Date();
		}

		if(dataInicial.after(dataFinal)) {
			erro = "A data inicial não pode ser maior que a data final";
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(erro));
			throw new BusinessException(erro);
		} else {
			try {
				int limite = Integer.valueOf(Parametros.LIMITE_DIAS_GERACAO_RELATORIO.getValue());
				if (Days.daysBetween(new DateTime(dataInicial), new DateTime(dataFinal)).getDays() > limite) {
					erro = String.format("O limite máximo de dias para geração de relatório está configurado para %s dias. Não é possível realizar a busca", limite);
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(erro));
					throw new BusinessException(erro);
				}
			} catch (NumberFormatException e) {
				erro = "O parâmetro " + Parametros.LIMITE_DIAS_GERACAO_RELATORIO.getLabel() + " não está configurado ou está configurado incorretamente. Contate o administrador do sistema.";
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(erro));
				throw new BusinessException(erro);
			}
		}
	}

}
