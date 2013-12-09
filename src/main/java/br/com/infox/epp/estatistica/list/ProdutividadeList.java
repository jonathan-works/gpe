package br.com.infox.epp.estatistica.list;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.entity.UsuarioLogin;
import br.com.infox.epp.estatistica.bean.ProdutividadeBean;
import br.com.infox.epp.fluxo.entity.Fluxo;

@Name(ProdutividadeList.NAME)
@Scope(ScopeType.PAGE)
public class ProdutividadeList extends EntityList<ProdutividadeBean> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "produtividadeList";
	
	private static final String DEFAULT_EJBQL = "select new br.com.infox.epp.estatistica.bean.ProdutividadeBean("
			+ " o.tempoPrevisto, l.localizacao, p.nome, us.nome, t.tarefa,"
			+ " avg(o.tempoGasto), min(o.tempoGasto), max(o.tempoGasto), count(o)"
			+ " )"
			+ " from ProcessoEpaTarefa o"
			+ " inner join o.usuarioTaskInstance u"
			+ " inner join u.papel p"
			+ " inner join u.localizacao l"
			+ " inner join u.usuario us"
			+ " inner join o.tarefa t"
			+ " inner join o.processoEpa proc"
			+ " inner join proc.naturezaCategoriaFluxo ncf"
			+ " inner join ncf.fluxo f"
			+ " where us = #{produtividadeList.usuario}";
	
	private static final String DEFAULT_ORDER = "us.nome";
	private static final String R1 = "f = #{produtividadeList.fluxo}";
	private static final String R2 = "o.dataInicio >= #{produtividadeList.dataInicio}";
	private static final String R3 = "o.dataFim <= #{produtividadeList.dataFim}";
	
	private Fluxo fluxo;
	private UsuarioLogin usuario;
	private List<Fluxo> fluxos;
	private Date dataInicio;
	private Date dataFim;
	
	public ProdutividadeList() {
		setGroupBy("t, o.tempoPrevisto, l.localizacao, p.nome, us.nome");
	}

	@Override
	protected void addSearchFields() {
		addSearchField("f", SearchCriteria.IGUAL, R1);
		addSearchField("o.dataInicio", SearchCriteria.MAIOR_IGUAL, R2);
		addSearchField("o.dataFim", SearchCriteria.MENOR_IGUAL, R3);
	}

	@Override
	protected String getDefaultEjbql() {
		return DEFAULT_EJBQL;
	}

	@Override
	protected String getDefaultOrder() {
		return DEFAULT_ORDER;
	}

	@Override
	protected Map<String, String> getCustomColumnsOrder() {
		return null;
	}

	public Fluxo getFluxo() {
		return fluxo;
	}
	
	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
	}
	
	public UsuarioLogin getUsuario() {
		return usuario;
	}
	
	public void setUsuario(UsuarioLogin usuario) {
		this.usuario = usuario;
	}
	
	public Date getDataInicio() {
		return dataInicio;
	}
	
	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}
	
	public Date getDataFim() {
		return dataFim;
	}
	
	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	public List<Fluxo> getFluxoList() {
        if (fluxos == null) {
            fluxos = getEntityManager().createQuery("select o from Fluxo o order by o.fluxo", Fluxo.class).getResultList();
        }
        return fluxos;
    }
	
	public void clear() {
		this.fluxo = null;
		this.fluxos = null;
		this.dataFim = null;
		this.dataInicio = null;
		this.usuario = null;
	}
}
