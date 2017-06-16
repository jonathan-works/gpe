package br.com.infox.epp.processo.documento.manager;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtils;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.component.tree.LocalizacaoTreeHandler;
import br.com.infox.epp.access.crud.LocalizacaoCrudAction;
import br.com.infox.epp.access.entity.Localizacao;
import br.com.infox.epp.access.entity.Papel;
import br.com.infox.epp.access.manager.LocalizacaoManager;
import br.com.infox.epp.access.manager.PapelManager;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.processo.documento.entity.Documento;
import br.com.infox.epp.processo.documento.entity.Pasta;
import br.com.infox.epp.processo.documento.entity.PastaRestricao;
import br.com.infox.epp.processo.documento.list.DocumentoList;
import br.com.infox.epp.processo.documento.type.PastaRestricaoEnum;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.manager.MetadadoProcessoManager;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class PastaRestricaoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(PastaRestricaoAction.class);

	@Inject
	private PastaManager pastaManager;
	@Inject
	private ActionMessagesService actionMessagesService;
	@Inject
	private PastaRestricaoManager pastaRestricaoManager;
	@Inject
	private PapelManager papelManager;
	@Inject
	private LocalizacaoManager localizacaoManager;
	@Inject
	private InfoxMessages infoxMessages;
	@Inject
	private MetadadoProcessoManager metadadoProcessoManager;
	
    private DocumentoList documentoList = ComponentUtil.getComponent(DocumentoList.NAME);
    private StatusMessages statusMessage = ComponentUtil.getComponent(StatusMessages.COMPONENT_NAME);
	
	private Boolean adicionarPastaPadrao = false;
	private Boolean alvoRestricaoParticipante;
	private Boolean pastaSelecionada = false;
	private Boolean pastaSelecionadaPadrao = false;
	private Boolean semPasta = false;
	private List<Pasta> pastaList;
	private List<PastaRestricao> restricoes;
	private Localizacao alvoRestricaoLocalizacao;
	private Integer id;
	private Papel alvoRestricaoPapel;
	private Pasta instance;
	private Pasta padrao;
	private PastaRestricao restricaoInstance;
	private Processo processo;
	private Processo processoReal;
	private Map<Integer, Boolean> canRemoveMap = new HashMap<>();
	
	@PostConstruct
	public void create() {
	    clearInstances();
	    // Isto está aqui para evitar erro ao editar uma restrição do tipo localização na primeira vez que entra na tela,
        // causado pela injeção a este componente que
        // está presente em LocalizaccaoTreehandler.getEntityToIgnore
	    Beans.getReference(LocalizacaoCrudAction.class).newInstance();
	}
	
	protected void clearInstances() {
	    newInstance();
	    newRestricaoInstance();
	}

	protected void retrievePadrao() {
	    MetadadoProcesso metadadoPastaDefault = processoReal.getMetadado(EppMetadadoProvider.PASTA_DEFAULT);
	    padrao = metadadoPastaDefault == null ? null : (Pasta) metadadoPastaDefault.getValue();
	}

	public boolean isPastaDefault(Pasta pasta) {
	    return pasta.equals(padrao);
	}

    public void setProcesso(Processo processo) {
        this.processo = processo.getProcessoRoot();
        this.processoReal = processo;
        try {
            initPastaList(processo);
            clearInstances();
            retrievePadrao();
        } catch (DAOException e) {
            LOG.error(e);
            actionMessagesService.handleDAOException(e);
        }
    }

	protected void initPastaList(Processo processo) throws DAOException {
		this.pastaList = pastaManager.getByProcesso(processo.getProcessoRoot());
		semPasta = pastaList == null || pastaList.isEmpty();
	}

	public void selectPasta(Pasta pasta){
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            UIViewRoot viewRoot = context.getViewRoot();
            List<UIComponent> children = viewRoot.getChildren();
            resetInputValues(children);

            setInstance((Pasta) BeanUtils.cloneBean(pasta));
            pastaSelecionadaPadrao = isPastaDefault(getInstance());
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            LOG.error(e);
            statusMessage.add(Severity.INFO, "Erro ao selecionar pasta.");
        }
        setRestricoes(pasta);
        setPastaSelecionada(true);
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

    public void newInstance() {
		setInstance(new Pasta());
		getInstance().setRemovivel(true);
		getInstance().setSistema(false);
		setPastaSelecionada(false);
	}
	
	public void newRestricaoInstance() {
		LocalizacaoTreeHandler localizacaoTreeHandler = Beans.getReference(LocalizacaoTreeHandler.class);
		if(localizacaoTreeHandler != null) {
			localizacaoTreeHandler.clearTree(); 
		}
	    setRestricaoInstance(new PastaRestricao());
	    setAlvoRestricaoPapel(new Papel());
	    setAlvoRestricaoLocalizacao(new Localizacao());
	    setAlvoRestricaoParticipante(true);
	}

	public boolean validateNomePasta() {
	    String nome = getInstance().getNome();
	    Integer id = getInstance().getId();
	    for (Pasta pasta : getPastaList()) {
            if (nome.equals(pasta.getNome()) && !(id == pasta.getId())) {
	            return false;
	        }
	    }
	    return true;
	}
	
	public boolean validateOrdem() {
	    Integer ordem = getInstance().getOrdem();
	    Integer id = getInstance().getId();
	    for (Pasta pasta : getPastaList()) {
	        if (ordem == pasta.getOrdem() && !(id == pasta.getId())) {
	            return false;
	        }
	    }
	    return true;
	}
	
	private boolean prePersist() throws DAOException{
	    getInstance().setProcesso(processo);
		getInstance().setSistema(false);
		persistirNovaPasta();
		if (adicionarPastaPadrao || semPasta) {
		    setInstanceAsPastaPadrao();
		}
		setPastaList(pastaManager.getByProcesso(processo));
		setPastaSelecionada(false);
		setSemPasta(false);
		setAdicionarPastaPadrao(false);
		return true;
	}

	protected void persistirNovaPasta() throws DAOException {
		pastaManager.persistWithDefault(getInstance());
	}
	
	public void persistPasta() {
		try {
			if (prePersist()) {
				statusMessage.add(StatusMessage.Severity.INFO, "Pasta adicionada com sucesso.");
			}
			canRemoveMap.clear();
		} catch (DAOException e) {
		    LOG.error(e);
			actionMessagesService.handleDAOException(e);
		}
	}

	public void updatePasta() {
		try {
			atualizarPasta();
			if (pastaSelecionadaPadrao && !getInstance().equals(padrao)) {
			    setInstanceAsPastaPadrao();
			}
			statusMessage.add(Severity.INFO, "Pasta atualizada com sucesso.");
		} catch (DAOException e) {
		    LOG.error(e);
			actionMessagesService.handleDAOException(e);
		}
	}

	protected void atualizarPasta() throws DAOException {
		pastaManager.update(getInstance());
	}

	protected void setInstanceAsPastaPadrao() {
	    MetadadoProcesso metadadoPastaDefault = processoReal.getMetadado(EppMetadadoProvider.PASTA_DEFAULT);
	    if (metadadoPastaDefault == null) {
	        metadadoProcessoManager.addMetadadoProcesso(processoReal, EppMetadadoProvider.PASTA_DEFAULT, getInstance().getId().toString());
	    } else {
	        metadadoPastaDefault.setValor(getInstance().getId().toString());
	        metadadoProcessoManager.update(metadadoPastaDefault);
	    }
	    padrao = getInstance();
	    canRemoveMap.clear();
	}

	public void removePasta(Pasta pasta) {
		try {
			if (pastaManager == null) {
				pastaManager = ComponentUtil.getComponent(PastaManager.NAME);
			}
			documentoList.checkPastaToRemove(pasta);
			pastaManager.deleteComRestricoes(pasta);
			if (pasta.equals(getInstance())) {
			    newInstance();
			}
			if (pasta.equals(padrao)) {
			    removeMetadadoPadrao(pasta);
			    retrievePadrao();
			}
			initPastaList(processo);
			canRemoveMap.clear();
			FacesMessages.instance().add(Severity.INFO, "Pasta removida com sucesso.");
		} catch (DAOException e) {
		    LOG.error(e);
			actionMessagesService.handleDAOException(e);
		}
	}

	private void removeMetadadoPadrao(Pasta pasta) {
        pastaManager.removeMetadadoPadrao(pasta);
    }

    /**
	 * Verifica se uma pasta pode ser removida.
	 * A regra implementada é:
	 * <ul><li>O atributo 'removivel' da Pasta deve ser true</li>
	 *     <li>Não pode ter nenhum documento dentro da Pasta</li>
	 *     <li>Caso exista mais de uma pasta no processo, esta não pode ser a pasta padrão, observando inclusive os eventuais processos filhos</li></ul>
	 * @param pasta
	 * @return true, caso possa ser remomível, falso caso contrário.
	 */
	public Boolean canRemovePasta(Pasta pasta) {
	    if (canRemoveMap.containsKey(pasta.getId())) {
	        return canRemoveMap.get(pasta.getId());
	    }
	    Boolean response = false;
		if (pasta.getRemovivel()) {
			List<Documento> documentoList = pasta.getDocumentosList();
			response = (documentoList == null || documentoList.isEmpty())
			        && (pastaList.size() == 1 || !pastaManager.isPadraoEmAlgumProcesso(pasta));
		}
		canRemoveMap.put(pasta.getId(), response);
		return response;
	}

	public Boolean canEditRestricao(PastaRestricao restricao) {
	    return restricao.getPasta().getEditavel();
	}
	/**
	 * Mesmo que uma pasta seja editável, não se pode remover a restrição do tipo 'DEFAULT'
	 * 
	 * @param restricao
	 * @return true caso possa remover, falso caso contrário
	 */
	public Boolean canRemoveRestricao(PastaRestricao restricao) {
	    if (restricao.getPasta().getEditavel()) {
	        return !restricao.getTipoPastaRestricao().equals(PastaRestricaoEnum.D);
	    }
	    return false;
	}

	/**
	 * O usuário só pode editar as restrições daquelas pastas que foram marcadas como editáveis
	 * no modelo da pasta, dentro da configuração do fluxo
	 * 
	 * @return true caso possa editar, falso caso contrário.
	 */
	public Boolean canEditRestricoes() {
	    return getPastaSelecionada() && getInstance().getEditavel();
	}
	
	public List<PastaRestricaoEnum> getTiposRestricao() {
	    return PastaRestricaoEnum.getValuesSemDefault();
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

    private Boolean alreadyExists(PastaRestricao restricao) {
        List<PastaRestricao> restricoesExistentes = pastaRestricaoManager.getByPasta(getInstance());
        for (PastaRestricao restricaoExistente : restricoesExistentes) {
            if (!restricaoExistente.getId().equals(restricao.getId())
                    && restricaoExistente.getTipoPastaRestricao().equals(restricao.getTipoPastaRestricao())
                    && restricaoExistente.getAlvo().equals(restricao.getAlvo())) {
                return true;
            }
        }
        return false;
    }
    
    public void persistRestricao() {
        PastaRestricao restricao = getRestricaoInstance();
        if (alreadyExists(restricao)) {
            statusMessage.add(Severity.INFO, format(infoxMessages.get("pasta.restricao.alreadyExists"), getAlvoFormatado(restricao)));
        } else {
            restricao.setPasta(getInstance());
            try {
                pastaRestricaoManager.persist(restricao);
                getRestricoes().add(restricao);
                statusMessage.add(Severity.INFO, infoxMessages.get("pasta.restricao.added"));
            } catch (DAOException e) {
                LOG.error(e);
                actionMessagesService.handleDAOException(e);
            }
        }
	}
	
    public void removeRestricao(PastaRestricao restricao) {
        try {
            pastaRestricaoManager.remove(restricao);
            setRestricoes(getInstance());
        } catch (DAOException e) {
            LOG.error(e);
            actionMessagesService.handleDAOException(e);
        }
    }
    
    public void loadRestricao(PastaRestricao restricao) {
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
    
    public void updateRestricao() {
        PastaRestricao restricao = getRestricaoInstance();
        if (alreadyExists(restricao)) {
            statusMessage.add(Severity.INFO, format(infoxMessages.get("pasta.restricao.alreadyExists"), getAlvoFormatado(restricao)));
        } else {
            try {
                pastaRestricaoManager.update(restricao);
                statusMessage.add(Severity.INFO, infoxMessages.get("pasta.restricao.updated"));
            } catch (DAOException e) {
                LOG.error(e);
                actionMessagesService.handleDAOException(e);
            }
        }
    }
    
    public String getAlvoFormatado(PastaRestricao restricao) {
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
	
	public Pasta getInstance() {
		return instance;
	}

	public void setInstance(Pasta pasta) {
		this.instance = pasta;
	}

	public List<Pasta> getPastaList() {
		return pastaList;
	}

	public void setPastaList(List<Pasta> pastaList) {
		this.pastaList = pastaList;
	}

	public Processo getProcesso() {
		return processo;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
		setInstance(pastaManager.find(id));
	}

	public List<PastaRestricao> getRestricoes() {
		return restricoes;
	}

	private void setRestricoes(Pasta pasta) {
		this.restricoes = pastaRestricaoManager.getByPasta(pasta);
	}
	
	public Boolean getPastaSelecionada() {
		return pastaSelecionada;
	}

	public void setPastaSelecionada(Boolean pastaSelecionada) {
		this.pastaSelecionada = pastaSelecionada;
	}

    public PastaRestricao getRestricaoInstance() {
        return restricaoInstance;
    }

    public void setRestricaoInstance(PastaRestricao restricaoInstance) {
        this.restricaoInstance = restricaoInstance;
    }

    public Boolean getPastaSelecionadaPadrao() {
        return pastaSelecionadaPadrao;
    }

    public void setPastaSelecionadaPadrao(Boolean pastaSelecionadaPadrao) {
        this.pastaSelecionadaPadrao = pastaSelecionadaPadrao;
    }

    public Boolean getAdicionarPastaPadrao() {
        return adicionarPastaPadrao;
    }

    public void setAdicionarPastaPadrao(Boolean adicionarPastaPadrao) {
        this.adicionarPastaPadrao = adicionarPastaPadrao;
    }

    public Boolean getSemPasta() {
        return semPasta;
    }

    public void setSemPasta(Boolean semPasta) {
        this.semPasta = semPasta;
    }
}
