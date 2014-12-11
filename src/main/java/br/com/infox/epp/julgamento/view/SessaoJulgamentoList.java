package br.com.infox.epp.julgamento.view;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.core.list.EntityList;
import br.com.infox.core.list.SearchCriteria;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SessaoJulgamento;
import br.com.infox.epp.julgamento.manager.SalaManager;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;

@Scope(ScopeType.CONVERSATION)
@Name(SessaoJulgamentoList.NAME)
public class SessaoJulgamentoList extends EntityList<SessaoJulgamento> {
	
	private static final long serialVersionUID = 1L;
	public static final String NAME = "sessaoJulgamentoList";
	
	private static final String TEMPLATE = "/Julgamento/Colegiado/Sessao/sessaoJulgamentoTemplate.xls";
    private static final String DOWNLOAD_XLS_NAME = "SessaoJulgamento.xls";
	 
	private static final String DEFAULT_EJBQL = "select o from SessaoJulgamento o ";
	private static final String DEFAULT_ORDER = "data, horaInicio";
	
	@In
	private SalaManager salaManager;
	@In
	private UnidadeDecisoraColegiadaManager unidadeDecisoraColegiadaManager;
	
	private List<Sala> salas;
	private List<UnidadeDecisoraColegiada> colegiadas;
	private UnidadeDecisoraColegiada colegiada;
	private Date dataInicio;
	private Date dataFim;
	
	public void validate() {
		super.validate();
		this.colegiada = Authenticator.instance().getColegiadaLogada();
	}
	
	@Override
	public void newInstance() {
		super.newInstance();
		this.colegiada = null;
		this.salas = null;
	}
	
	@Override
	protected void addSearchFields() {
        addSearchField("data", SearchCriteria.DATA_IGUAL);
        addSearchField("horaInicio", SearchCriteria.MAIOR_IGUAL);
        addSearchField("horaFim", SearchCriteria.MENOR_IGUAL);
        addSearchField("sala", SearchCriteria.IGUAL);
        addSearchField("ativo", SearchCriteria.IGUAL);
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
		Map<String, String> orderMap = new HashMap<>();
		orderMap.put("colegiada", "o.sala.unidadeDecisoraColegiada");
		orderMap.put("data", "o.data");
		orderMap.put("sala", "o.sala.nome");
		orderMap.put("statusSessao", "statusSessao");
		orderMap.put("ativo", "ativo");
		return orderMap;
	}
	
	@Override
    public String getTemplate() {
        return TEMPLATE;
    }

    @Override
    public String getDownloadXlsName() {
        return DOWNLOAD_XLS_NAME;
    }
	
	public void onChangeColegiada() {
		this.salas = null;
	}

	public List<Sala> getSalas() {
		if ( salas == null ) {
			if ( colegiada != null ) {
				salas = salaManager.listSalaByColegiada(colegiada);
			} else {
				salas = salaManager.findAll();
			}
		}
		return salas;
	}
	
	public List<UnidadeDecisoraColegiada> getColegiadas() {
		if ( colegiadas == null ) {
			colegiadas = unidadeDecisoraColegiadaManager.findAll();
		}
		return colegiadas;
	}

	public UnidadeDecisoraColegiada getColegiada() {
		return colegiada;
	}

	public void setColegiada(UnidadeDecisoraColegiada colegiada) {
		this.colegiada = colegiada;
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
	

}
