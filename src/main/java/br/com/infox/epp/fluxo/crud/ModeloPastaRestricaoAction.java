package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPastaRestricao;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.ModeloPastaManager;
import br.com.infox.epp.fluxo.manager.ModeloPastaRestricaoManager;

@Name(ModeloPastaRestricaoAction.NAME)
@Scope(ScopeType.PAGE)
@Transactional
public class ModeloPastaRestricaoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	static final String NAME = "modeloPastaRestricaoAction";

	@In
	private FluxoManager fluxoManager;
	@In
	private ModeloPastaManager modeloPastaManager;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private ModeloPastaRestricaoManager modeloPastaRestricaoManager;

	private ModeloPasta instance;
	private List<ModeloPasta> modeloPastaList;
	private Fluxo fluxo;
	private Integer id;
	private List<ModeloPastaRestricao> restricoes;
	private Boolean pastaSelecionada = false;

	@Create
	public void create() {
		newInstance();
	}

	public void newInstance() {
		setInstance(new ModeloPasta());
		setRemovivel(true);
		setSistema(false);
	}
	
	private boolean prePersist() throws DAOException{
		String nome = getNome();
		for (ModeloPasta modeloPasta : getModeloPastaList()){
			if(nome.equals(modeloPasta.getNome())){
				return false;
			}
		}
		getInstance().setFluxo(fluxo);
		setSistema(false);
		modeloPastaManager.persistWithDefault(getInstance());
		setModeloPastaList(modeloPastaManager.getByFluxo(fluxo));
		setPastaSelecionada(false);
		return true;
	}

	public ModeloPastaManager getModeloPastaManager() {
		return modeloPastaManager;
	}

	public void setModeloPastaManager(ModeloPastaManager modeloPastaManager) {
		this.modeloPastaManager = modeloPastaManager;
	}

	public ModeloPastaRestricaoManager getModeloPastaRestricaoManager() {
		return modeloPastaRestricaoManager;
	}

	public void setModeloPastaRestricaoManager(ModeloPastaRestricaoManager modeloPastaRestricaoManager) {
		this.modeloPastaRestricaoManager = modeloPastaRestricaoManager;
	}

	public ModeloPasta getInstance() {
		return instance;
	}

	public void setInstance(ModeloPasta instance) {
		this.instance = instance;
	}

	public List<ModeloPasta> getModeloPastaList() {
		return modeloPastaList;
	}

	public void setModeloPastaList(List<ModeloPasta> modeloPastaList) {
		this.modeloPastaList = modeloPastaList;
	}

	public List<ModeloPastaRestricao> getRestricoes() {
		return restricoes;
	}

	public void setRestricoes(List<ModeloPastaRestricao> restricoes) {
		this.restricoes = restricoes;
	}

	public Boolean getPastaSelecionada() {
		return pastaSelecionada;
	}

	public void setPastaSelecionada(Boolean pastaSelecionada) {
		this.pastaSelecionada = pastaSelecionada;
	}

	public void setEditavel(Boolean editavel) {
		getInstance().setEditavel(editavel);
	}

	public Boolean getEditavel() {
		return getInstance().getEditavel();
	}

	public void setRemovivel(Boolean removivel) {
		getInstance().setRemovivel(removivel);
	}

	public Boolean getRemovivel() {
		return getInstance().getRemovivel();
	}

	public String getNome() {
		return getInstance().getNome();
	}

	public void setNome(String nome) {
		this.getInstance().setNome(nome);
	}

	public Boolean getSistema() {
		return getInstance().getSistema();
	}

	public void setSistema(Boolean sistema) {
		getInstance().setSistema(sistema);
	}

}
