package br.com.infox.epp.julgamento.view;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.type.Displayable;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SessaoJulgamento;
import br.com.infox.epp.julgamento.entity.StatusSessaoJulgamento;
import br.com.infox.epp.julgamento.manager.SalaManager;
import br.com.infox.epp.julgamento.manager.SessaoJulgamentoManager;
import br.com.infox.epp.julgamento.manager.StatusSessaoJulgamentoManager;
import br.com.infox.epp.julgamento.type.Periodicidade;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;

@Name(SessaoJulgamentoCrudAction.NAME)
public class SessaoJulgamentoCrudAction extends AbstractCrudAction<SessaoJulgamento, SessaoJulgamentoManager> {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "sessaoJulgamentoCrudAction";
	
	@In
	private SalaManager salaManager;
	@In
	private UnidadeDecisoraColegiadaManager unidadeDecisoraColegiadaManager;
	@In
	private StatusSessaoJulgamentoManager statusSessaoJulgamentoManager;
	
	private List<Sala> salas;
	private List<UnidadeDecisoraColegiada> colegiadas;
	private List<StatusSessaoJulgamento> statusSessaoJulgamentos;
	private UnidadeDecisoraColegiada colegiada;
	private Periodicidade periodicidade;
	private TipoPeriodicidade tipoPeriodicidade;
	private Object valorPeriodicidade;
	
	@Override
	public void init() {
		super.init();
		this.colegiada = Authenticator.instance().getColegiadaLogada();
	}
	
	public Periodicidade[] getPeriodicidades() {
		return Periodicidade.values();
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
	
	public List<StatusSessaoJulgamento> getStatusSessaoJulgamentos() {
		if ( statusSessaoJulgamentos == null ) {
			statusSessaoJulgamentos = statusSessaoJulgamentoManager.findAll();
		}
		return statusSessaoJulgamentos;
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
		
		D("Até Data"), Q("Quantidade Repetições");
		
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
