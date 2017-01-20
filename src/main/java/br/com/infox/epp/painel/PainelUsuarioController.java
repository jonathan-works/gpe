package br.com.infox.epp.painel;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

import org.jboss.seam.faces.Redirect;
import org.richfaces.event.DropEvent;

import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.epp.access.api.Authenticator;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.cdi.exception.ExceptionHandled;
import br.com.infox.epp.cdi.exception.ExceptionHandled.MethodType;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.painel.caixa.CaixaManager;
import br.com.infox.epp.processo.consulta.list.ConsultaProcessoList;
import br.com.infox.epp.processo.situacao.manager.SituacaoProcessoManager;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.tarefa.component.tree.PainelEntityNode;
import br.com.infox.epp.tarefa.component.tree.PainelTreeHandler;
import br.com.infox.ibpm.task.manager.TaskInstanceManager;
import br.com.infox.seam.security.SecurityUtil;

@Named
@ViewScoped
public class PainelUsuarioController implements Serializable {

	public static final String NUMERO_PROCESSO_FILTERED = "numeroProcessoFiltered";

	private static final long serialVersionUID = 1L;
	
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
		setNumeroProcesso(getNumeroProcessoFromSession());
		loadTipoProcessoDisponiveis();
		loadFluxosDisponiveis();
	}
	
	private String getNumeroProcessoFromSession(){
		Map<String, Object> sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
		return (String) sessionMap.get(NUMERO_PROCESSO_FILTERED);
	}
	
	public void changePerfil() throws IOException{
		limparFiltros();
		atualizarPainelProcessos();
	}
	
	public void atualizarPainelProcessos() throws IOException {
	    List<FluxoBean> fluxosDisponiveisTemp = situacaoProcessoManager.getFluxos(tipoProcessoDisponiveis, getNumeroProcesso());
	    verificaHouveAlteracao(fluxosDisponiveisTemp);
	}
	
	protected void verificaHouveAlteracao(List<FluxoBean> fluxosDisponiveisTemp) throws IOException {
	    ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
	    if (fluxosDisponiveisTemp.size() != fluxosDisponiveis.size()) {
            FacesContext.getCurrentInstance().getExternalContext().redirect(servletContext.getContextPath() + "/Painel/list.seam");
        } else {
            fluxosDisponiveisTemp.removeAll(fluxosDisponiveis);
            if (!fluxosDisponiveisTemp.isEmpty()) {
                FacesContext.getCurrentInstance().getExternalContext().redirect(servletContext.getContextPath() + "/Painel/list.seam");
            }
        }
	    fluxosDisponiveisTemp.clear();
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
		situacaoProcessoManager.loadTasks(getSelectedFluxo());
	}
	
    @ExceptionHandled(value = MethodType.UNSPECIFIED)
	public void atribuirTarefa(TaskBean taskBean) {
        taskInstanceManager.atribuirTarefa(Long.valueOf(taskBean.getIdTaskInstance()));
        taskBean.setAssignee(Authenticator.getUsuarioLogado().getLogin());
	}
	
	@ExceptionHandled(value = MethodType.UNSPECIFIED)
	public void liberarTarefa(TaskBean taskBean) {
	    taskInstanceManager.removeUsuario(Long.valueOf(taskBean.getIdTaskInstance()));
        taskBean.setAssignee(null);

    }
    
	public void onSelectNode() {
		consultaProcessoList.onSelectNode(getSelected());
    }
	
	public String getTaskNodeKey() {
	    return getSelected().getId().toString();
	}

	@SuppressWarnings("unchecked")
	@ExceptionHandled(value = MethodType.UNSPECIFIED)
	public void moverProcessoParaCaixaDropEventListener(DropEvent evt) {
		Caixa caixa = caixaManager.find((Integer) evt.getDropValue());
		Object dragValue = evt.getDragValue();
		if (dragValue instanceof TaskBean) {
			moverProcessoParaCaixa((TaskBean) dragValue, caixa);
		} else if (dragValue instanceof List<?>) {
			moverProcessosParaCaixa((List<TaskBean>) dragValue, caixa);
		}
	}
	
	private void moverProcessoParaCaixa(TaskBean taskBean, Caixa caixa) {
        caixaManager.moverProcessoParaCaixa(taskBean.getIdProcesso(), caixa);
        getSelected().moverParaCaixa(taskBean, caixa);
        painelTreeHandler.clearTree();
    }
	
	private void moverProcessosParaCaixa(List<TaskBean> taskBeans, Caixa caixa) {
	    for (TaskBean taskBean : taskBeans) {
	        caixaManager.moverProcessoParaCaixa(taskBean.getIdProcesso(), caixa);
            getSelected().moverParaCaixa(taskBean, caixa);
	    }
	    painelTreeHandler.clearTree();
	}
	
	@ExceptionHandled(value = MethodType.REMOVE)
	public void removerCaixa(PainelEntityNode painelEntityNode) {
        Integer idCaixa = (Integer) painelEntityNode.getEntity().getId();
        caixaManager.remove(idCaixa);
        TaskDefinitionBean taskDefinitionBean = (TaskDefinitionBean) painelEntityNode.getParent().getEntity();
        taskDefinitionBean.removerCaixa(idCaixa);
        painelTreeHandler.clearTree();
    }
	
	@ExceptionHandled(value = MethodType.PERSIST)
	public void adicionarCaixa(ActionEvent event) {
	    String inputNomeCaixa = (String) event.getComponent().getAttributes().get("inputNomeCaixa");
	    String nomeCaixa = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(inputNomeCaixa);
        Caixa caixa = new Caixa();
        caixa.setTaskKey(getSelected().getId().toString());
        caixa.setNomeCaixa(nomeCaixa);
        caixaManager.persist(caixa);
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
		setSelectedFluxo(null);
		painelTreeHandler.clearTree();
		loadFluxosDisponiveis();
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(NUMERO_PROCESSO_FILTERED, getNumeroProcesso());
	}
	
	public void limparFiltros(){
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(NUMERO_PROCESSO_FILTERED);
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
