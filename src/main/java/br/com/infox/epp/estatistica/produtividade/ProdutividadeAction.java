package br.com.infox.epp.estatistica.produtividade;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.exception.ExcelExportException;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.util.ExcelExportUtil;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.cdi.seam.ContextDependency;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.log.Log;
import br.com.infox.log.Logging;
import br.com.infox.seam.path.PathResolver;

@Name(ProdutividadeAction.NAME)
@Scope(ScopeType.CONVERSATION)
@ContextDependency
public class ProdutividadeAction implements Serializable {

    private static final long serialVersionUID = 1L;
    public static final String NAME = "produtividadeAction";
    private static final String XLS_TEMPLATE = "/BAM/Produtividade/ProdutividadeTemplate.xls";
    private static final Log LOG = Logging.getLog(ProdutividadeAction.class);

    @In
    private ProdutividadeManager produtividadeManager;

    @In
    private FluxoManager fluxoManager;
    @In
    private InfoxMessages infoxMessages;

    private Fluxo fluxo;
    private UsuarioLogin usuario;
    private List<Fluxo> fluxos;
    private Date dataInicio;
    private Date dataFim;
    private List<ProdutividadeBean> produtividades;

    private Long resultCount;
    private Integer page = 1;
    private Long pageCount;
    private Integer maxResults = 15;

    public Fluxo getFluxo() {
        return fluxo;
    }

    public void setFluxo(Fluxo fluxo) {
        this.fluxo = fluxo;
        refreshQuery();
    }

    public UsuarioLogin getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioLogin usuario) {
        this.usuario = usuario;
        refreshQuery();
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
        refreshQuery();
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
        refreshQuery();
    }

    public List<Fluxo> getFluxoList() {
        if (fluxos == null) {
            fluxos = fluxoManager.getFluxosAtivosList();
        }
        return fluxos;
    }

    public void clear() {
        this.fluxo = null;
        this.fluxos = null;
        this.dataFim = null;
        this.dataInicio = null;
        this.usuario = null;
        refreshQuery();
    }

    public List<ProdutividadeBean> list(int max) {
        if (this.produtividades == null || this.maxResults != max) {
            this.maxResults = max;
            Map<String, Object> params = buildParams();
            this.produtividades = produtividadeManager.listProdutividade(params);
        }
        return this.produtividades;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
        refreshQuery();
    }

    public Long getPageCount() {
        return pageCount;
    }

    public void setPageCount(Long pageCount) {
        this.pageCount = pageCount;
    }

    public boolean isPreviousExists() {
        return this.pageCount != null && this.pageCount > 1;
    }

    public boolean isNextExists() {
        return isPreviousExists();
    }

    public Long getResultCount() {
        if (this.resultCount == null) {
            this.resultCount = produtividadeManager.totalProdutividades(buildParams());

            this.pageCount = this.resultCount / this.maxResults;
            if (this.resultCount % this.maxResults != 0) {
                this.pageCount++;
            }
        }
        return this.resultCount;
    }

    public String getCriteria() {
        StringBuilder sb = new StringBuilder("Usuário: ");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        if (usuario != null) {
            sb.append(this.usuario.getNomeUsuario());
        }
        if (fluxo != null) {
            sb.append("\nFluxo: ");
            sb.append(this.fluxo.getFluxo());
        }
        if (dataInicio != null && dataFim != null) {
            sb.append("\nPeríodo: De ");
            sb.append(sdf.format(dataInicio));
            sb.append(" até ");
            sb.append(sdf.format(dataFim));
        } else if (dataInicio != null) {
            sb.append("\nPeríodo: A partir de ");
            sb.append(sdf.format(dataInicio));
        } else if (dataFim != null) {
            sb.append("\nPeríodo: Até ");
            sb.append(sdf.format(dataFim));
        }

        return sb.toString();
    }

    public String getTemplate() {
        return XLS_TEMPLATE;
    }

    public void exportarXLS() {
        setPage(1);
        List<ProdutividadeBean> beanList = list(10000);
        try {
            if (beanList == null || beanList.isEmpty()) {
                FacesMessages.instance().add(Severity.INFO, infoxMessages.get("entity.noDataAvailable"));
            } else {
                exportarXLS(getTemplate(), beanList);
            }
        } catch (ExcelExportException e) {
            LOG.error(".exportarXLS()", e);
            FacesMessages.instance().add(Severity.ERROR, "Erro ao exportar arquivo." + e.getMessage());
        }
    }

    private void exportarXLS(String template, List<ProdutividadeBean> beanList) throws ExcelExportException {
        PathResolver pathResolver = (PathResolver) Component.getInstance(PathResolver.NAME);
        String urlTemplate = pathResolver.getContextRealPath() + template;
        Map<String, Object> map = new HashMap<String, Object>();
        StringBuilder className = new StringBuilder(ProdutividadeBean.class.getSimpleName());
        className = className.replace(0, 1, className.substring(0, 1).toLowerCase());
        map.put(className.toString(), beanList);
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        map.put("numberFormat", numberFormat);
        ExcelExportUtil.downloadXLS(urlTemplate, map, "Produtividade.xls");
    }

    private Map<String, Object> buildParams() {
        Map<String, Object> params = new HashMap<>();
        params.put(ProdutividadeQuery.PARAM_START, this.maxResults
                * (this.page - 1));
        params.put(ProdutividadeQuery.PARAM_COUNT, this.maxResults);
        if (this.usuario != null) {
            params.put(ProdutividadeQuery.PARAM_USUARIO, this.usuario);
        }
        if (this.fluxo != null) {
            params.put(ProdutividadeQuery.PARAM_FLUXO, this.fluxo);
        }
        if (this.dataInicio != null) {
            params.put(ProdutividadeQuery.PARAM_DATA_INICIO, this.dataInicio);
        }
        if (this.dataFim != null) {
            params.put(ProdutividadeQuery.PARAM_DATA_FIM, this.dataFim);
        }
        return params;
    }

    private void refreshQuery() {
        this.produtividades = null;
        this.resultCount = null;
    }
}
