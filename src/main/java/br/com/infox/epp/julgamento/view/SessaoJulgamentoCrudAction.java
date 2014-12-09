package br.com.infox.epp.julgamento.view;

import java.util.List;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.crud.AbstractCrudAction;
import br.com.infox.core.suggest.AbstractSuggestBean;
import br.com.infox.core.type.Displayable;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.julgamento.entity.Sala;
import br.com.infox.epp.julgamento.entity.SessaoJulgamento;
import br.com.infox.epp.julgamento.entity.StatusSessaoJulgamento;
import br.com.infox.epp.julgamento.manager.SalaManager;
import br.com.infox.epp.julgamento.manager.SessaoJulgamentoManager;
import br.com.infox.epp.julgamento.manager.StatusSessaoJulgamentoManager;
import br.com.infox.epp.julgamento.type.Periodicidade;
import br.com.infox.epp.pessoa.entity.PessoaFisica;
import br.com.infox.epp.unidadedecisora.entity.UnidadeDecisoraColegiada;
import br.com.infox.epp.unidadedecisora.manager.UnidadeDecisoraColegiadaManager;
import br.com.infox.seam.exception.BusinessException;

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
	@In
	private ActionMessagesService actionMessagesService;
	
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
	
	@Override
	public String save() {
		String ret = "";
		try {
			ret = super.save();
		} catch (BusinessException e) {
			getMessagesHandler().add(e.getMessage());
		}
		return ret;
	}
	
	@Override
	protected void beforeSave() {
		super.beforeSave();
		if (isManaged()) { return; }
		getManager().beforeSave(getInstance(), periodicidade, valorPeriodicidade);
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
	
	public AbstractSuggestBean<PessoaFisica> getSuggestPessoaFisicaBean() {
		return suggestPessoaFisicaBean;
	}

	private AbstractSuggestBean<PessoaFisica> suggestPessoaFisicaBean = new AbstractSuggestBean<PessoaFisica>() {

		private static final long serialVersionUID = 1L;

		@Override
		public PessoaFisica load(Object id) {
			return entityManager.find(PessoaFisica.class, id);
		}

		@Override
		public String getEjbql(String typed) {
			StringBuilder sb = new StringBuilder();
		    sb.append("select new br.com.infox.componentes.suggest.SuggestItem(o.idPessoa, (o.cpf || ' - ' ||o.nome)) ");
		    sb.append("from PessoaFisica o where o.ativo = true ");
		    if (typed.matches("\\d+")) {
				sb.append(" o.cpf = :").append(INPUT_PARAMETER).append(" ");
			} else {
				sb.append("and lower(o.nome) like lower(concat('%', :").append(INPUT_PARAMETER).append(", '%')) ");
			}
		    sb.append("order by o.nome");
		    return sb.toString();
		}
	};

	enum TipoPeriodicidade implements Displayable {
		
		D("Data Até"), Q("Quantidade Repetições");
		
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
