package br.com.infox.epp.painel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Tuple;

import org.jboss.seam.faces.Redirect;
import org.richfaces.event.DropEvent;

import br.com.infox.componentes.column.DynamicColumnModel;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.messages.InfoxMessages;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.painel.caixa.CaixaManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.consulta.list.ConsultaProcessoList;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.situacao.manager.SituacaoProcessoManager;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.infox.epp.tarefa.component.tree.PainelEntityNode;
import br.com.infox.epp.tarefa.component.tree.PainelTreeHandler;
import br.com.infox.epp.tarefa.manager.TarefaManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.security.SecurityUtil;
import br.com.infox.seam.util.ComponentUtil;

@Named
@ViewScoped
public class PainelUsuarioController implements Serializable {

	private static final long serialVersionUID = 1L;
	protected static final String NAME = "painelUsuarioController";
	private static final String DYNAMIC_COLUMN_EXPRESSION = "#{painelUsuarioController.getVariavelProcesso(row, '%s', %s).valor}";
	private static final LogProvider LOG = Logging.getLogProvider(PainelUsuarioController.class);
	
	@Inject
	private SituacaoProcessoManager situacaoProcessoManager;
	@Inject
	protected InfoxMessages infoxMessages;
	@Inject
	protected SecurityUtil securityUtil;
	@Inject
	private PainelTreeHandler painelTreeHandler;
	@Inject
	protected DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;

	private VariavelProcessoService variavelProcessoService = ComponentUtil.getComponent(VariavelProcessoService.NAME);
	private CaixaManager caixaManager = ComponentUtil.getComponent(CaixaManager.NAME);
	private ActionMessagesService actionMessagesService = ComponentUtil.getComponent(ActionMessagesService.NAME);
	private ConsultaProcessoList consultaProcessoList = ComponentUtil.getComponent(ConsultaProcessoList.NAME);

	private List<Integer> processoIdList;
	private List<DynamicColumnModel> dynamicColumns;
	private FluxoBean selectedFluxo;
	protected List<FluxoBean> fluxosDisponiveis;
	private List<TipoProcesso> tipoProcessoDisponiveis;
	private boolean exibirColunasPadrao = true;
	private Boolean expedida;
	private String idProcessDefinition;

	@PostConstruct
	protected void init() {
		loadTipoProcessoDisponiveis();
		loadFluxosDisponiveis();
	}

	private void loadFluxosDisponiveis() {
		fluxosDisponiveis = situacaoProcessoManager.getFluxosDisponiveis(tipoProcessoDisponiveis);
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
		consultaProcessoList.newInstance();
	}

	public void onSelectNode() {
		processoIdList = null;
		dynamicColumns = null;
		updateDatatable();
	}

	public Integer getIdCaixa() {
		if (PainelEntityNode.CAIXA_TYPE.equals(getSelectedType())) {
			return getSelected().get("idCaixa", Integer.class);
		}
		return null;
	}

	private String getSelectedType() {
		return getSelected() != null ? getSelected().get("type", String.class) : null;
	}

	public List<Integer> getProcessoIdList() {
		if (getSelected() != null && getSelectedFluxo() != null) {
			if (processoIdList == null) {
				processoIdList = situacaoProcessoManager.getIdProcessosAbertosByIdTarefa(getSelected(), 
						selectedFluxo.getTipoProcesso(), getSelectedFluxo().getExpedida());
			}
			return processoIdList;
		}
		return null;
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
		processoIdList = null;
	}

	public void editarCaixa() {
		Redirect r = new Redirect();
		r.setViewId("/Caixa/listView.xhtml");
		r.setParameter("tab", "form");
		r.setParameter("id", getSelected().get("idCaixa"));
		r.execute();
	}

	public Integer getTaskId() {
		if (getSelected() != null) {
			return getSelected().get("idTask", Integer.class);
		}
		return null;
	}

	public Integer getTarefaId() {
		if (getSelected() != null) {
			return getSelected().get("idTarefa", Integer.class);
		}
		return null;
	}

	public Tuple getSelected() {
		return painelTreeHandler.getSelected();
	}

	public void refresh() {
		painelTreeHandler.refresh();
	}

	private void updateDatatable() {
		Integer idTarefa = painelTreeHandler.getTarefaId();
		if (idTarefa != null) {
			consultaProcessoList.newInstance();
			TarefaManager tarefaManager = ComponentUtil.getComponent(TarefaManager.NAME);
			consultaProcessoList.setTarefa(tarefaManager.find(idTarefa));
			List<Integer> idsProcesso = getProcessoIdList();
			if (idsProcesso != null && !idsProcesso.isEmpty()) {
				Integer idProcesso = idsProcesso.get(0);
				List<DefinicaoVariavelProcesso> definicoes = definicaoVariavelProcessoManager.getDefinicaoVariavelProcessoVisivelPainel(idProcesso);
				dynamicColumns = new ArrayList<>();
				for (DefinicaoVariavelProcesso definicao : definicoes) {
					DynamicColumnModel columnModel = new DynamicColumnModel(definicao.getLabel(), String.format(DYNAMIC_COLUMN_EXPRESSION,
							definicao.getNome(), idTarefa));
					dynamicColumns.add(columnModel);
				}
			}
		}
	}

	public VariavelProcesso getVariavelProcesso(Processo processo, String nome, Integer idTarefa) {
		return variavelProcessoService.getVariavelProcesso(processo, nome, idTarefa);
	}

	public List<DynamicColumnModel> getDynamicColumns() {
		if (dynamicColumns == null) {
			updateDatatable();
		}
		return dynamicColumns;
	}

	public String getDestinatarioComunicacao(Processo processo) {
		DestinatarioModeloComunicacao destinatario = processo.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
		return destinatario.getNome();
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
			int index = fluxosDisponiveis.indexOf(new FluxoBean(idProcessDefinition, expedida));
			fluxoBean = fluxosDisponiveis.get(index);
		}
		setSelectedFluxo(fluxoBean);
		onSelectFluxo();
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
}
