package br.com.infox.epp.painel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.jboss.seam.bpm.Actor;
import org.jboss.seam.bpm.ManagedJbpmContext;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Redirect;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.richfaces.event.DropEvent;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.transaction.Transactional;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.painel.caixa.CaixaManager;
import br.com.infox.epp.processo.consulta.list.ConsultaProcessoList;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.situacao.manager.SituacaoProcessoManager;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.component.tree.PainelTreeHandler;
import br.com.infox.ibpm.task.manager.TaskInstanceManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.security.SecurityUtil;

@Named
@ViewScoped
public class PainelUsuarioController implements Serializable {

	private static final long serialVersionUID = 1L;
	protected static final String NAME = "painelUsuarioController";
	private static final LogProvider LOG = Logging.getLogProvider(PainelUsuarioController.class);
	
	@Inject
	private SituacaoProcessoManager situacaoProcessoManager;
	@Inject
	protected InfoxMessages infoxMessages;
	@Inject
	protected SecurityUtil securityUtil;
	@Inject
	protected PainelTreeHandler painelTreeHandler;
	@Inject
	protected ConsultaProcessoList consultaProcessoList;
	@Inject
	protected CaixaManager caixaManager;
	@Inject
	protected ActionMessagesService actionMessagesService;
	@Inject
	private TaskInstanceManager taskInstanceManager;

	private FluxoBean selectedFluxo;
	protected List<FluxoBean> fluxosDisponiveis;
	private List<TipoProcesso> tipoProcessoDisponiveis;
	private boolean exibirColunasPadrao = true;
	private Boolean expedida;
	private String numeroProcesso;
	private String idProcessDefinition;

	@PostConstruct
	protected void init() {
		setNumeroProcesso(null);
		loadTipoProcessoDisponiveis();
		loadFluxosDisponiveis();
	}
	
	public void atualizarPainelProcessos() throws IOException {
//	    List<FluxoBean> fluxosDisponiveisTemp = situacaoProcessoManager.getFluxosDisponiveis(tipoProcessoDisponiveis, getNumeroProcesso());
//	    verificaHouveAlteracao(fluxosDisponiveisTemp);
	}
	
	protected void verificaHouveAlteracao(List<FluxoBean> fluxosDisponiveisTemp) throws IOException {
	    if (fluxosDisponiveisTemp.size() != fluxosDisponiveis.size()) {
            FacesContext.getCurrentInstance().getExternalContext().redirect("list.seam");
        } else {
            fluxosDisponiveisTemp.removeAll(fluxosDisponiveis);
            if (!fluxosDisponiveisTemp.isEmpty()) {
                FacesContext.getCurrentInstance().getExternalContext().redirect("list.seam");
            }
        }
	}

	private void loadFluxosDisponiveis() {
		fluxosDisponiveis = situacaoProcessoManager.getFluxos(tipoProcessoDisponiveis, getNumeroProcesso());
	}

	protected void loadTipoProcessoDisponiveis() {
		tipoProcessoDisponiveis = new ArrayList<>(4);
		tipoProcessoDisponiveis.add(null);
		if (hasRecursoPainelComunicacaoEletronica()) {
			tipoProcessoDisponiveis.add(TipoProcesso.COMUNICACAO);
		}
		if (hasRecursoPainelComunicacaoNaoEletronica()) {
			tipoProcessoDisponiveis.add(TipoProcesso.COMUNICACAO_NAO_ELETRONICA);
		}
		if (hasRecursoPainelDocumento()) {
			tipoProcessoDisponiveis.add(TipoProcesso.DOCUMENTO);
		}
	}

	public List<FluxoBean> getFluxosDisponiveis() {
		return fluxosDisponiveis;
	}
	
	public void onSelectFluxo() {
		painelTreeHandler.clearTree();
		painelTreeHandler.setFluxoBean(getSelectedFluxo());
		consultaProcessoList.onSelectFluxo(getSelectedFluxo());
	}
	
	@Transactional
	public void atribuirTarefa(TaskBean taskBean) {
        try {
            TaskInstance taskInstance = ManagedJbpmContext.instance().getTaskInstance(taskBean.getIdTaskInstance());
            ManagedJbpmContext.instance().getSession().buildLockRequest(LockOptions.READ).setLockMode(LockMode.PESSIMISTIC_FORCE_INCREMENT).lock(taskInstance);
            taskInstance.setAssignee(Actor.instance().getId());
            taskBean.setAssignee(taskInstance.getAssignee());
        } catch (Exception e) {
            LOG.error("painelUsuarioController.atribuirTarefa(taskBean)", e);
        }
	}
	
	public void liberarTarefa(Long idTaskInstance) {
        try {
            taskInstanceManager.removeUsuario(idTaskInstance);
            TaskBean taskBean = getSelectedFluxo().getTask(idTaskInstance.toString());
            if (taskBean != null) {
                taskBean.setAssignee(null);
            }
            FacesMessages.instance().add("Tarefa Liberada com Sucesso!");
        } catch (Exception e) {
            LOG.error("painelUsuarioController.removeUsuario(idTaskInstance)", e);
        }
    }

	public void onSelectNode() {
		consultaProcessoList.onSelectNode(getSelected());
	}
	
	public String getTaskNodeKey() {
	    return getSelected().getId().toString();
	}

	@SuppressWarnings("unchecked")
	public void moverProcessoParaCaixaDropEventListener(DropEvent evt) {
		Caixa caixa = caixaManager.find((Integer) evt.getDropValue());
		try {
			if (evt.getDragValue() instanceof Processo) {
				Processo processo = (Processo) evt.getDragValue();
				caixaManager.moverProcessoParaCaixa(processo, caixa);
			} else if (evt.getDragValue() instanceof List<?>) {
				List<Processo> processos = (List<Processo>) evt.getDragValue();
				caixaManager.moverProcessosParaCaixa(processos, caixa);
			}
		} catch (DAOException e) {
			LOG.error("moverProcessoParaCaixaDropEventListener", e);
			actionMessagesService.handleDAOException(e);
		}
		painelTreeHandler.clearTree();
	}

	public void editarCaixa() {
		Redirect r = new Redirect();
		r.setViewId("/Caixa/listView.xhtml");
		r.setParameter("tab", "form");
		r.setParameter("id", getSelected().getId());
		r.execute();
	}

	public PanelDefinition getSelected() {
		return painelTreeHandler.getSelected();
	}

	public void refresh() {
		painelTreeHandler.refresh();
	}
	
	public void adicionarFiltroNumeroProcessoRoot(){
		loadTipoProcessoDisponiveis();
		loadFluxosDisponiveis();
		painelTreeHandler.clearTree();
		setSelectedFluxo(null);
	}
	
	public void limparFiltros(){
		init();
		painelTreeHandler.clearTree();
		setSelectedFluxo(null);
	}

	public FluxoBean getSelectedFluxo() {
		return selectedFluxo;
	}

	public void setSelectedFluxo(FluxoBean selectedFluxo) {
		this.selectedFluxo = selectedFluxo;
	}

	public void setIdProcessDefinition(String idProcessDefinition) {
		this.idProcessDefinition = idProcessDefinition;
	}
	
	public void setExpedida(Boolean expedida) {
		this.expedida = expedida;
	}

	public void selectFluxo() {
		FluxoBean fluxoBean = null;
		if (idProcessDefinition != null) {
    		for (FluxoBean fluxoBeanDisponivel : fluxosDisponiveis) {
                if (fluxoBeanDisponivel.getProcessDefinitionId().equals(idProcessDefinition) 
                                && fluxoBeanDisponivel.getExpedida().equals(expedida)){
                    fluxoBean = fluxoBeanDisponivel;
                    break;
                }
            }
		}
		setSelectedFluxo(fluxoBean);
		onSelectFluxo();
	}
	
	public boolean canShowProcessoList() {
	    return getSelected() != null;
	}
	
	public boolean hasRecursoPainelComunicacaoEletronica() {
		return securityUtil.checkPage("/pages/Painel/Comunicacao/painel.seam");
	}

	public boolean hasRecursoPainelComunicacaoNaoEletronica() {
		return hasRecursoPainelComunicacaoEletronica() && hasRecursoPainelComunicacaoExpedida();
	}
	
	public boolean hasRecursoPainelComunicacaoRecebida() {
		return securityUtil.checkPage("/pages/Painel/comunicacoesRecebidas.seam");
	}
	
	public boolean hasRecursoPainelComunicacaoExpedida() {
		return securityUtil.checkPage("/pages/Painel/comunicacoesExpedidas.seam");
	}

	public boolean hasRecursoPainelDocumento() {
		return securityUtil.checkPage("/pages/Painel/fluxoDocumento.seam");
	}

	public boolean isShowPainelProcessosComum() {
		return getSelectedFluxo() != null && getSelectedFluxo().getTipoProcesso() == null && getSelectedFluxo().getProcessDefinitionId() != null 
				&& !getSelectedFluxo().isBpmn20();
	}

	public boolean isShowPainelComunicacoesEletronicasComum() {
		return getSelectedFluxo() != null && TipoProcesso.COMUNICACAO.equals(selectedFluxo.getTipoProcesso()) && getSelectedFluxo().getProcessDefinitionId() != null
				&& !getSelectedFluxo().isBpmn20();
	}

	public boolean isShowPainelComunicacoesNaoEletronicasComum() {
		return getSelectedFluxo() != null && TipoProcesso.COMUNICACAO_NAO_ELETRONICA.equals(selectedFluxo.getTipoProcesso()) && getSelectedFluxo().getProcessDefinitionId() != null
				&& !getSelectedFluxo().isBpmn20();
	}

	public boolean isShowPainelDocumentosComum() {
		return getSelectedFluxo() != null && TipoProcesso.DOCUMENTO.equals(selectedFluxo.getTipoProcesso()) && getSelectedFluxo().getProcessDefinitionId() != null
				&& !getSelectedFluxo().isBpmn20();
	}
	
	public boolean isShowTarefasTree() {
		return getSelectedFluxo() != null;
	}
	
	public boolean isShowFiltroInfo() {
		return getNumeroProcesso() != null && !getNumeroProcesso().isEmpty();
	}
	
	public List<TipoProcesso> getTipoProcessoDisponiveis() {
		return tipoProcessoDisponiveis;
	}

	public String getLocaleTitleKey() {
	    return "painel.fluxos";
	}
	
	public boolean isExibirColunasPadrao() {
		return exibirColunasPadrao;
	}
	
	public void setExibirColunasPadrao(boolean exibirColunasPadrao) {
		this.exibirColunasPadrao = exibirColunasPadrao;
	}
	
	public String getNumeroProcesso() {
		return numeroProcesso;
	}

	public void setNumeroProcesso(String numeroProcesso) {
		this.numeroProcesso = numeroProcesso;
	}
}
