package br.com.infox.epp.fluxo.crud;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPastaRestricao;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.ModeloPastaManager;
import br.com.infox.epp.fluxo.manager.ModeloPastaRestricaoManager;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.manager.PastaManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.ibpm.process.definition.ProcessBuilder;

@Name(ModeloPastaRestricaoAction.NAME)
@Scope(ScopeType.PAGE)
@Transactional
public class ModeloPastaRestricaoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	static final String NAME = "modeloPastaRestricaoAction";
	private static final LogProvider LOG = Logging.getLogProvider(ModeloPastaRestricaoAction.class);
	
	@In
	private FluxoManager fluxoManager;
	@In
	private ModeloPastaManager modeloPastaManager;
	@In
	private ActionMessagesService actionMessagesService;
	@In
	private ModeloPastaRestricaoManager modeloPastaRestricaoManager;
	@In
	private ProcessBuilder processBuilder;
	
	private ModeloPasta instance;
	private List<ModeloPasta> modeloPastaList;
	private Fluxo fluxo;
	private Integer id;
	private List<ModeloPastaRestricao> restricoes;
	private Boolean modeloPastaSelecionada = false;

	@Create
	public void create() {
		newInstance();
		setFluxo(processBuilder.getFluxo());
	}

	public void newInstance() {
		setInstance(new ModeloPasta());
		setRemovivel(true);
		setSistema(false);
		setModeloPastaSelecionada(false);
	}
	
	private boolean prePersist() throws DAOException{
		String nome = getNome();
		Integer ordem = getOrdem();
		getInstance().setFluxo(getFluxo());
		for (ModeloPasta modeloPasta : getModeloPastaList()){
			if(nome.equals(modeloPasta.getNome())){
				FacesMessages.instance().add(Severity.INFO, "Já existe um Modelo de Pasta com este nome.");				
				return false;
			}
			if (ordem.equals(modeloPasta.getOrdem())) {
				FacesMessages.instance().add(Severity.INFO, "Já existe um Modelo de Pasta com esta ordem.");
				return false;
			}
		}
		setSistema(false);		
		return true;
	}
	
	public void persist() {
		try {
			if (prePersist()) {
				modeloPastaManager.persistWithDefault(getInstance());
				setModeloPastaList(modeloPastaManager.getByFluxo(getFluxo()));
				setModeloPastaSelecionada(false);
				FacesMessages.instance().add(StatusMessage.Severity.ERROR, "Modelo de Pasta adicionado com sucesso.");
			}
		} catch (DAOException e) {
			LOG.error(e);
			actionMessagesService.handleDAOException(e);
		}
	}
	
	public void update() {
	
	}
	
	public void remove(Pasta pasta) {
		
	}

	public void selectModeloPasta(ModeloPasta modeloPasta){
		setInstance(modeloPasta);
		setRestricoes(modeloPasta);
		setModeloPastaSelecionada(true);
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

	private void setRestricoes(ModeloPasta modeloPasta) {
		this.restricoes = modeloPastaRestricaoManager.getByModeloPasta(modeloPasta);
	}

	public Boolean getModeloPastaSelecionada() {
		return modeloPastaSelecionada;
	}

	public void setModeloPastaSelecionada(Boolean pastaSelecionada) {
		this.modeloPastaSelecionada = pastaSelecionada;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		setInstance(modeloPastaManager.find(id));
	}

	public Fluxo getFluxo() {
		return fluxo;
	}

	public void setFluxo(Fluxo fluxo) {
		this.fluxo = fluxo;
		setModeloPastaList(modeloPastaManager.getByFluxo(this.fluxo));
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
	
	public String getDescricao(){
		return this.getInstance().getDescricao();
	}
	
	public void setDescricao(String descricao){
		this.getInstance().setDescricao(descricao);
	}

	public Boolean getSistema() {
		return getInstance().getSistema();
	}

	public void setSistema(Boolean sistema) {
		getInstance().setSistema(sistema);
	}
	
	public void setOrdem(Integer ordem){
		getInstance().setOrdem(ordem);
	}
	
	public Integer getOrdem(){
		return getInstance().getOrdem();
	}

}
