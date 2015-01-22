package br.com.infox.epp.julgamento.view;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.core.type.Displayable;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SessaoJulgamento;
import br.com.infox.epp.julgamento.manager.SalaManager;
import br.com.infox.epp.julgamento.manager.SessaoJulgamentoManager;
import br.com.infox.epp.julgamento.manager.StatusSessaoJulgamentoManager;
import br.com.infox.epp.julgamento.type.Periodicidade;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;
import br.com.infox.seam.exception.BusinessException;

@Name(SessaoJulgamentoCrudAction.NAME)
public class SessaoJulgamentoCrudAction extends AbstractCrudAction<SessaoJulgamento, SessaoJulgamentoManager> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "sessaoJulgamentoCrudAction";
	public static final String STATUS_SESSAO_PREVISTA = "prevista";
	private static final LogProvider LOG = Logging.getLogProvider(SessaoJulgamentoCrudAction.class);
	
	@In
	private SalaManager salaManager;
	@In
	private UnidadeDecisoraColegiadaManager unidadeDecisoraColegiadaManager;
	@In
	private StatusSessaoJulgamentoManager statusSessaoJulgamentoManager;
	@In
	private ActionMessagesService actionMessagesService;
	
	private List<Sala> salas;
	private List<UnidadeDecisoraColegiada> colegiadas;
	private UnidadeDecisoraColegiada colegiada;
	private Periodicidade periodicidade;
	private TipoPeriodicidade tipoPeriodicidade;
	private Object valorPeriodicidade;
	
	@Override
	public void init() {
		super.init();
		this.colegiada = Authenticator.instance().getColegiadaLogada();
	}
	
	@Override
	public void newInstance() {
		super.newInstance();
		getInstance().setStatusSessao(statusSessaoJulgamentoManager.getStatusSessaoJulgamentoByNome(STATUS_SESSAO_PREVISTA));
		this.salas = null;
		this.periodicidade = null;
		this.valorPeriodicidade = null;
	}
	
	@Override
	protected boolean isInstanceValid() {
		try {
			getManager().validate(getInstance());
			return super.isInstanceValid();
		} catch (BusinessException e) {
			getMessagesHandler().add(e.getMessage());
			return false;
		}
	}
	
	@Override
	protected void afterSave(String ret) {
		super.afterSave(ret);
		try {
			getManager().afterSave(getInstance(), getPeriodicidade(), valorPeriodicidade);
		} catch (CloneNotSupportedException | DAOException e) {
			LOG.error("afterSave(ret)", e);
		}
	}
	
	public Periodicidade[] getPeriodicidades() {
		return Periodicidade.values();
	}
	
	public TipoPeriodicidade[] getTiposPeriodicidade() {
		return TipoPeriodicidade.values();
	}
	
	public void clearValorPeriodicidade() {
		setValorPeriodicidade(null);
	}
	
	public void onChangeColegiada() {
		this.salas = null;
		getInstance().setSala(null);
	}

	public List<Sala> getSalas() {
		if ( salas == null ) {
			if ( colegiada != null ) {
				salas = salaManager.listSalaByColegiada(colegiada);
			} else {
				salas = salaManager.findAllAtivo();
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

	public Periodicidade getPeriodicidade() {
		return periodicidade;
	}

	public void setPeriodicidade(Periodicidade periodicidade) {
		this.periodicidade = periodicidade;
	}
	
	public TipoPeriodicidade getTipoPeriodicidade() {
		return tipoPeriodicidade;
	}

	public void setTipoPeriodicidade(TipoPeriodicidade tipoPeriodicidade) {
		this.tipoPeriodicidade = tipoPeriodicidade;
	}

	public Object getValorPeriodicidade() {
		return valorPeriodicidade;
	}

	public void setValorPeriodicidade(Object valorPeriodicidade) {
		this.valorPeriodicidade = valorPeriodicidade;
	}
	
	enum TipoPeriodicidade implements Displayable {
		
		D("Data Até"), Q("Quantidade");
		
		private String label;
		
		private TipoPeriodicidade(String label) {
			this.label = label;
		}
		
		@Override
		public String getLabel() {
			return label;
		}
	}
	
}
