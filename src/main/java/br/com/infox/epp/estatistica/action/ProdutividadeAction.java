package br.com.infox.epp.estatistica.action;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.estatistica.bean.ProdutividadeBean;
import br.com.infox.epp.estatistica.manager.ProdutividadeManager;
import br.com.infox.epp.estatistica.query.ProdutividadeQuery;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.FluxoManager;

@Name(ProdutividadeAction.NAME)
@Scope(ScopeType.CONVERSATION)
public class ProdutividadeAction implements Serializable {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "produtividadeAction";
	
	@In
	private ProdutividadeManager produtividadeManager;
	
	@In
	private FluxoManager fluxoManager;
	
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
            fluxos = fluxoManager.getFluxoList();
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

	private Map<String, Object> buildParams() {
		Map<String, Object> params = new HashMap<>();
		params.put(ProdutividadeQuery.PARAM_START, this.maxResults * (this.page - 1));
		params.put(ProdutividadeQuery.PARAM_COUNT, this.maxResults);
		params.put(ProdutividadeQuery.PARAM_USUARIO, this.usuario);
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
