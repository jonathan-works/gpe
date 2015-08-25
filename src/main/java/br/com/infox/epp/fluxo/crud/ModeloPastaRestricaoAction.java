package br.com.infox.epp.fluxo.crud;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.entity.ModeloPasta;
import br.com.infox.epp.fluxo.entity.ModeloPastaRestricao;
import br.com.infox.epp.fluxo.list.ModeloPastaList;
import br.com.infox.epp.fluxo.manager.FluxoManager;
import br.com.infox.epp.fluxo.manager.ModeloPastaManager;
import br.com.infox.epp.fluxo.manager.ModeloPastaRestricaoManager;
import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Name(ModeloPastaRestricaoAction.NAME)
@Scope(ScopeType.PAGE)
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
    @In(create = true)
    private ModeloPastaList modeloPastaList;
    @In
    private PapelManager papelManager;
    @In
    private LocalizacaoManager localizacaoManager;
    @In
    private InfoxMessages infoxMessages;
    @In(StatusMessages.COMPONENT_NAME)
    private StatusMessages statusMessage;

	
	private ModeloPasta instance;
	private List<ModeloPasta> listModeloPastas;
	private Fluxo fluxo;
	private Integer id;
	private List<ModeloPastaRestricao> restricoes;
	private ModeloPastaRestricao restricaoInstance;
	
	private Papel alvoRestricaoPapel;
	private Localizacao alvoRestricaoLocalizacao;
	private Boolean alvoRestricaoParticipante;
	
	public void init(final Fluxo fluxo) {
		newInstance();
		newRestricaoInstance();
		setFluxo(fluxo);
		// Isto está aqui para evitar erro ao editar uma restrição do tipo localização na primeira vez que entra na tela,
		// causado pela injeção a este componente que
		// está presente em LocalizaccaoTreehandler.getEntityToIgnore
		ComponentUtil.<LocalizacaoCrudAction>getComponent(LocalizacaoCrudAction.NAME).newInstance();
	}

	public void newInstance() {
		setInstance(new ModeloPasta());
		getInstance().setRemovivel(true);
		getInstance().setEditavel(true);
		getInstance().setSistema(true);
	}
	
	public void newRestricaoInstance() {
	    ModeloPastaRestricao restricaoInstance = new ModeloPastaRestricao();
		setRestricaoInstance(restricaoInstance);
	    setAlvoRestricaoPapel(new Papel());
	    setAlvoRestricaoLocalizacao(new Localizacao());
	    setAlvoRestricaoParticipante(true);
	}
	
	private boolean prePersist() throws DAOException{
		getInstance().setFluxo(getFluxo());
		getInstance().setSistema(false);		
		return true;
	}
	
	public void persist() {
		try {
			if (prePersist()) {
				modeloPastaManager.persistWithDefault(getInstance());
				getFluxo().getModeloPastaList().add(getInstance());
				setListModeloPastas(modeloPastaManager.getByFluxo(getFluxo()));
				newInstance();
				statusMessage.add(StatusMessage.Severity.INFO, infoxMessages.get("modeloPasta.added"));
			}
			newInstance();
		} catch (DAOException e) {
			LOG.error(e);
			actionMessagesService.handleDAOException(e);
		}
	}
	
	public void update() {
		try {
			modeloPastaManager.update(getInstance());
			statusMessage.add(StatusMessage.Severity.INFO, infoxMessages.get("modeloPasta.updated"));
		} catch (DAOException e) {
			LOG.error(e);
			actionMessagesService.handleDAOException(e);
		}
	}
	
	public void removeModeloPasta(ModeloPasta modelo) {
		try {
			if (modeloPastaManager == null) {
				modeloPastaManager = ComponentUtil.getComponent(ModeloPastaManager.NAME);
			}
			modeloPastaManager.deleteComRestricoes(modelo);
			if (modelo.equals(getInstance())) {
			    newInstance();
			}
			setListModeloPastas(modeloPastaManager.getByFluxo(getFluxo()));
			statusMessage.add(Severity.INFO, infoxMessages.get("modeloPasta.removed"));
		} catch (DAOException e) {
			LOG.error(e);
			actionMessagesService.handleDAOException(e);
		}
	}

	public void selectModeloPasta(ModeloPasta modeloPasta){
		try {
		    FacesContext context = FacesContext.getCurrentInstance();
		    UIViewRoot viewRoot = context.getViewRoot();
		    List<UIComponent> children = viewRoot.getChildren();
		    resetInputValues(children);
		    
			setInstance((ModeloPasta)BeanUtils.cloneBean(modeloPasta));
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			LOG.error(e);
			actionMessagesService.handleException("", e);
		}
		setRestricoes(modeloPasta);
	}
	
	private void resetInputValues(List<UIComponent> children) {
	    for (UIComponent component : children) {
            if (component.getChildCount() > 0) {
                resetInputValues(component.getChildren());
            } else {
                if (component instanceof EditableValueHolder) {
                    EditableValueHolder input = (EditableValueHolder) component;
                    input.resetValue();
                }
            }
        }
	}
	
	public void persistRestricao() {
	    ModeloPastaRestricao restricao = getRestricaoInstance();
	    if (restricaoAlreadyExists(restricao)) {
	        statusMessage.add(Severity.INFO, format(infoxMessages.get("modeloPasta.restricao.alreadyExists"), getAlvoFormatado(restricao)));
	    } else {
    	    restricao.setModeloPasta(getInstance());
    	    try {
                modeloPastaRestricaoManager.persist(restricao);
                getRestricoes().add(restricao);
                statusMessage.add(StatusMessage.Severity.INFO, infoxMessages.get("modeloPasta.restricao.added"));
            } catch (DAOException e) {
            	LOG.error(e);
                actionMessagesService.handleDAOException(e);
            }
	    }
	}
	
    private boolean restricaoAlreadyExists(ModeloPastaRestricao restricao) {
        List<ModeloPastaRestricao> restricoesExistentes = modeloPastaRestricaoManager.getByModeloPasta(getInstance());
        for (ModeloPastaRestricao restricaoExistente : restricoesExistentes) {
            if (!restricaoExistente.getId().equals(restricao.getId())
                    && restricaoExistente.getTipoPastaRestricao().equals(restricao.getTipoPastaRestricao())
                    && restricaoExistente.getAlvo().equals(restricao.getAlvo())) {
                return true;
            }
        }
        return false;
    }

    public void removeModeloRestricao(ModeloPastaRestricao restricao) {
        try {
            modeloPastaRestricaoManager.remove(restricao);
            setRestricoes(getInstance());
        } catch (DAOException e) {
        	LOG.error(e);
            actionMessagesService.handleDAOException(e);
        }
    }

    public void updateRestricao() {
        ModeloPastaRestricao restricao = getRestricaoInstance();
        if (restricaoAlreadyExists(restricao)) {
            statusMessage.add(Severity.ERROR, format(infoxMessages.get("modeloPasta.restricao.alreadyExists"), getAlvoFormatado(restricao)));
        } else {
            try {
                modeloPastaRestricaoManager.update(restricao);
                statusMessage.add(StatusMessage.Severity.ERROR, infoxMessages.get("modeloPasta.restricao.updated"));
            } catch (DAOException e) {
            	LOG.error(e);
                actionMessagesService.handleDAOException(e);
            }
        }
    }
    
	public void loadRestricao(ModeloPastaRestricao restricao){
	     setRestricaoInstance(restricao);
	     PastaRestricaoEnum tipo = restricao.getTipoPastaRestricao();
	     if (PastaRestricaoEnum.P.equals(tipo)) {
	         setAlvoRestricaoPapel(papelManager.find(restricao.getAlvo()));
	     } else if (PastaRestricaoEnum.L.equals(tipo)) {
	         setAlvoRestricaoLocalizacao(localizacaoManager.find(restricao.getAlvo()));
	     } else if (PastaRestricaoEnum.R.equals(tipo)) {
	         setAlvoRestricaoParticipante(restricao.getAlvo() == 1);
	     }
	}
	
	public List<PastaRestricaoEnum> getTiposRestricao() {
		return PastaRestricaoEnum.getValuesSemDefault();
	}
	
	public String getAlvoFormatado(ModeloPastaRestricao restricao) {
        if (PastaRestricaoEnum.D.equals(restricao.getTipoPastaRestricao())) {
            return (infoxMessages.get("pasta.restricao.alvoTodos"));
        } else if (PastaRestricaoEnum.P.equals(restricao.getTipoPastaRestricao())) {
            return papelManager.find(restricao.getAlvo()).toString();
        } else if (PastaRestricaoEnum.R.equals(restricao.getTipoPastaRestricao())) {
            return restricao.getAlvo() == 1 ? infoxMessages.get("pasta.restricao.participantes") : infoxMessages.get("pasta.restricao.naoParticipantes");
        } else if (PastaRestricaoEnum.L.equals(restricao.getTipoPastaRestricao())) {
            return localizacaoManager.find(restricao.getAlvo()).getCaminhoCompletoFormatado(); 
        }
        return null;
    }
    
	public List<Papel> getAlvoPapelList() {
	    return papelManager.getPapeisOrdemAlfabetica();
	}
	
	public ModeloPasta getInstance() {
		return instance;
	}

	public void setInstance(ModeloPasta instance) {
		this.instance = instance;
	}

	public List<ModeloPasta> getListModeloPastas() {
		return listModeloPastas;
	}

	public void setListModeloPastas(List<ModeloPasta> modeloPastaList) {
		this.listModeloPastas = modeloPastaList;
	}
	
	public ModeloPastaRestricao getRestricaoInstance() {
        return restricaoInstance;
    }

    public void setRestricaoInstance(ModeloPastaRestricao restricaoInstance) {
        this.restricaoInstance = restricaoInstance;
    }

	public Boolean isRestricaoDefault(ModeloPastaRestricao modelo) {
	    return PastaRestricaoEnum.D.equals(modelo.getTipoPastaRestricao());
	}
	
	public Boolean isRestricaoDefault() {
	    return PastaRestricaoEnum.D.equals(getRestricaoInstance().getTipoPastaRestricao());
	}
	
	public Boolean isRestricaoLocalizacao() {
        return PastaRestricaoEnum.L.equals(getRestricaoInstance().getTipoPastaRestricao());
    }
	
	public Boolean isRestricaoPapel() {
        return PastaRestricaoEnum.P.equals(getRestricaoInstance().getTipoPastaRestricao());
    }
	
	public Boolean isRestricaoParticipante() {
        return PastaRestricaoEnum.R.equals(getRestricaoInstance().getTipoPastaRestricao());
    }
	
	public Boolean hideSelecione(){
		return getRestricaoInstance().getTipoPastaRestricao() != null;
	}
	
	public Papel getAlvoRestricaoPapel() {
	    return alvoRestricaoPapel; 
	}
	
	public void setAlvoRestricaoPapel(Papel papel) {
	    if (isRestricaoPapel()) {
	        getRestricaoInstance().setAlvo(papel.getIdPapel());
	    } else {
	        this.alvoRestricaoPapel = null;
	        return;
	    }
	    this.alvoRestricaoPapel = papel;
	}
	
	public Localizacao getAlvoRestricaoLocalizacao() {
        return alvoRestricaoLocalizacao;
    }

    public void setAlvoRestricaoLocalizacao(Localizacao localizacao) {
        if (isRestricaoLocalizacao() && localizacao != null) {
            getRestricaoInstance().setAlvo(localizacao.getIdLocalizacao());
        } else {
            this.alvoRestricaoLocalizacao = null;
            return;
        }
        this.alvoRestricaoLocalizacao = localizacao;
    }

    public Boolean getAlvoRestricaoParticipante() {
        return alvoRestricaoParticipante;
    }

    public void setAlvoRestricaoParticipante(Boolean inParticipante) {
        if (isRestricaoParticipante()) {
            getRestricaoInstance().setAlvo(inParticipante ? 1 : 0);
        }
        this.alvoRestricaoParticipante = inParticipante;
    }
    
	public List<ModeloPastaRestricao> getRestricoes() {
		return restricoes;
	}

	private void setRestricoes(ModeloPasta modeloPasta) {
		this.restricoes = modeloPastaRestricaoManager.getByModeloPasta(modeloPasta);
	}

	public boolean getModeloPastaSelecionada() {
		return instance == null || instance.getId() != null;
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
		modeloPastaList.getEntity().setFluxo(fluxo);
		setListModeloPastas(modeloPastaManager.getByFluxo(this.fluxo));
	}

}
