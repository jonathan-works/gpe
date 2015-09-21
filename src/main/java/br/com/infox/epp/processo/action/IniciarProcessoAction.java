package br.com.infox.epp.processo.action;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.hibernate.TypeMismatchException;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.international.StatusMessages;

import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.estatistica.type.SituacaoPrazoEnum;
import br.com.infox.epp.fluxo.bean.ItemBean;
import br.com.infox.epp.fluxo.entity.Categoria;
import br.com.infox.epp.fluxo.entity.CategoriaItem;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.entity.Natureza;
import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.metadado.entity.MetadadoProcesso;
import br.com.infox.epp.processo.metadado.system.MetadadoProcessoProvider;
import br.com.infox.epp.processo.metadado.type.EppMetadadoProvider;
import br.com.infox.epp.processo.partes.controller.ParticipantesController;
import br.com.infox.epp.processo.service.IniciarProcessoService;
import br.com.infox.epp.tarefa.entity.ProcessoTarefa;
import br.com.infox.epp.tarefa.manager.ProcessoTarefaManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.exception.BusinessException;

@Name(IniciarProcessoAction.NAME)
@Scope(ScopeType.CONVERSATION)
@Transactional
public class IniciarProcessoAction implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String NAME = "iniciarProcessoAction";
    private static final LogProvider LOG = Logging.getLogProvider(IniciarProcessoAction.class);

    @In
    private IniciarProcessoService iniciarProcessoService;
    @In
    private InfoxMessages infoxMessages;
    @In
    private ProcessoTarefaManager processoTarefaManager;

    private boolean renderedByItem;
    private boolean renderizarCadastroPartes;
    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    private Item itemDoProcesso;
    private Processo processo;
    private List<ItemBean> itemList;
    private Integer idTarefa;

    private String viewId;

    public void iniciarProcesso() {
    	newProcessoEpa();
    	if (itemDoProcesso != null) {
    		addItemDoProcesso(processo);
    	}
        enviarProcessoParaJbpm();
    }
    
    private void addItemDoProcesso(Processo processo) {
    	MetadadoProcessoProvider metadadoProcessoProvider = new MetadadoProcessoProvider(processo);
		MetadadoProcesso metadadoProcesso = metadadoProcessoProvider.gerarMetadado(EppMetadadoProvider.ITEM_DO_PROCESSO, itemDoProcesso.getIdItem().toString());
		processo.getMetadadoProcessoList().add(metadadoProcesso);
	}

	public void iniciarProcesso(Processo processo) {
    	setProcesso(processo);
    	enviarProcessoParaJbpm();
    }
    
    public void newProcessoEpa() {
        processo = new Processo();
        processo.setSituacaoPrazo(SituacaoPrazoEnum.SAT);
        processo.setNumeroProcesso("");
        processo.setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
        processo.setLocalizacao(Authenticator.getLocalizacaoAtual());
        processo.setUsuarioCadastro(Authenticator.getUsuarioLogado());
        processo.setDataInicio(new Date());
    }

    private void enviarProcessoParaJbpm() {
        try {
            iniciarProcessoService.iniciarProcesso(processo);
            ProcessoTarefa processoTarefa = processoTarefaManager.getUltimoProcessoTarefa(processo);
            getMessagesHandler().add("Processo inserido com sucesso!");
            if (processoTarefa == null) {
            	throw new BusinessException("Processo não está em tarefa humana");
            }
            idTarefa = processoTarefa.getTarefa().getIdTarefa();
        } catch (TypeMismatchException tme) {
            sendIniciarProcessoErrorMessage(IniciarProcessoService.TYPE_MISMATCH_EXCEPTION, tme);
        } catch (NullPointerException npe) {
            sendIniciarProcessoErrorMessage("Nenhum processo informado.", npe);
        } catch (DAOException e) {
            sendIniciarProcessoErrorMessage("Erro ao inserir o processo: "
                    + e.getMessage(), e);
        } catch (Exception e) {
        	if (e.getCause() instanceof BusinessException) {
        		try {
        			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        			ec.getFlash().put("message", e.getCause().getLocalizedMessage());
        			ec.redirect("listView.seam");
				} catch (IOException e1) {
					LOG.warn(e1.getMessage(), e1);
				}
        	}
        }
    }

    private void sendIniciarProcessoErrorMessage(String message, Exception exception) {
        LOG.error(".iniciarProcesso()", exception);
        getMessagesHandler().add(Severity.ERROR, infoxMessages.get(message));
    }

    public void onSelectNatCatFluxo(final NaturezaCategoriaFluxo ncf) {
        naturezaCategoriaFluxo = ncf;
        itemList = new ArrayList<ItemBean>();
        final Categoria categoria = naturezaCategoriaFluxo.getCategoria();
        if (categoria != null) {
            for (CategoriaItem ca : categoria.getCategoriaItemList()) {
                itemList.add(new ItemBean(ca.getItem()));
            }
            if (itemList.isEmpty()) {
                if (!necessitaPartes()) {
                    iniciarProcesso();
                    redirectIfExternalUser();
                } else {
                    renderizarCadastroPartes = Boolean.TRUE;
                    renderedByItem = Boolean.FALSE;
                    ParticipantesController.instance().setNaturezaCategoriaFluxo(ncf);
                }
            } else {
                setRenderedByItem(Boolean.TRUE);
            }
        }
    }

    public void onSelectItem(final ItemBean bean) {
        itemDoProcesso = bean.getItem();
        renderedByItem = hasSelectedItem();
        if (!necessitaPartes()) {
            iniciarProcesso();
            redirectIfExternalUser();
        } else {
            renderizarCadastroPartes = Boolean.TRUE;
            renderedByItem = Boolean.FALSE;
            ParticipantesController.instance().setNaturezaCategoriaFluxo(naturezaCategoriaFluxo);
        }
    }

    private void redirectIfExternalUser() {
        if (Authenticator.instance().isUsuarioExterno()) {
            final Redirect redirect = Redirect.instance();
            redirect.setViewId("/Processo/movimentar.seam");
            redirect.setParameter("scid", Conversation.instance().getId());
            redirect.setParameter("idProcesso", getProcesso().getIdProcesso());
            redirect.execute();
        }
    }

    private boolean hasSelectedItem() {
        for (ItemBean ib : itemList) {
            if (ib.isChecked()) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    private StatusMessages getMessagesHandler() {
        return FacesMessages.instance();
    }

    public boolean isRenderedByItem() {
        return renderedByItem;
    }

    public void setRenderedByItem(final boolean renderedByItem) {
        this.renderedByItem = renderedByItem;
    }

    public Processo getProcesso() {
		return processo;
	}

	public void setProcesso(Processo processo) {
		this.processo = processo;
	}

	public List<ItemBean> getItemList() {
        return itemList;
    }

    public void setItemList(final List<ItemBean> itemList) {
        this.itemList = itemList;
    }

    public Natureza getNatureza() {
        return naturezaCategoriaFluxo != null ? naturezaCategoriaFluxo.getNatureza() : null;
    }

    public boolean necessitaPartes() {
        return getNatureza() != null && getNatureza().getHasPartes();
    }

    public boolean isRenderizarCadastroPartes() {
        return renderizarCadastroPartes;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }
    
    public Integer getIdTarefa() {
		return idTarefa;
	}
}
