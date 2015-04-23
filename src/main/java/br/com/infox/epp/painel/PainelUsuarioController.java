package br.com.infox.epp.painel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Tuple;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.faces.Redirect;
import org.richfaces.event.DropEvent;

import br.com.infox.componentes.column.DynamicColumnModel;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.controller.AbstractController;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.painel.caixa.CaixaManager;
import br.com.infox.epp.processo.comunicacao.ComunicacaoMetadadoProvider;
import br.com.infox.epp.processo.comunicacao.DestinatarioModeloComunicacao;
import br.com.infox.epp.processo.consulta.list.ConsultaProcessoList;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.infox.epp.tarefa.component.tree.PainelEntityNode;
import br.com.infox.epp.tarefa.component.tree.PainelTreeHandler;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

@Name(PainelUsuarioController.NAME)
public class PainelUsuarioController extends AbstractController {
	
    private static final long serialVersionUID = 1L;

    public static final String NAME = "painelUsuarioController";
    private static final String DYNAMIC_COLUMN_EXPRESSION = "#{painelUsuarioController.getVariavelProcesso(row, '%s').valor}";
    private static final LogProvider LOG = Logging.getLogProvider(PainelUsuarioController.class);

    @In
    private ConsultaProcessoList consultaProcessoList;
    @In
    private ProcessoManager processoManager;
    @In
    private SituacaoProcessoDAO situacaoProcessoDAO;
    @In
    private VariavelProcessoService variavelProcessoService;
    @In
    private DefinicaoVariavelProcessoManager definicaoVariavelProcessoManager;
    @In
    private CaixaManager caixaManager;
    @In
    private ActionMessagesService actionMessagesService;
    @In
    private PainelTreeHandler painelTreeHandler;
    
    private Tuple selected;
    private List<Integer> processoIdList;
    private List<DynamicColumnModel> dynamicColumns;
    private TipoProcesso tipoProcesso;
    private String tabComunicacaoEletronica;

    @Observer("selectedTarefasTree")
    public void onSelected(Tuple selected) {
        setSelected(selected);
        processoIdList = null;
        dynamicColumns = null;
        updateDatatable();
    }
    
    private void onTabChange() {
    	processoIdList = null;
    	selected = null;
    	setTipoProcesso();
    	painelTreeHandler.clearTree();
    	painelTreeHandler.setTipoProcesso(getTipoProcesso());
    	painelTreeHandler.setTabComunicacoesExpedidas(isTabComunicacoesExpedidas());
    }

    /**
     * Método protected para poder ser sobrescrito pelos módulos do epp
     * */
	protected void setTipoProcesso() {
		setTipoProcesso(TipoProcesso.getByName(getTab()));
	}

    public Integer getIdCaixa() {
        if (PainelEntityNode.CAIXA_TYPE.equals(getSelectedType())) {
            return selected.get("idCaixa", Integer.class);
        }
        return null;
    }
    
    private String getSelectedType(){
    	return selected != null ? selected.get("type", String.class) : null;
    }

    public List<Integer> getProcessoIdList() {
        if (selected != null && !PainelEntityNode.FLUXO_TYPE.equals(getSelectedType())) {
            if (processoIdList == null) {
                processoIdList = situacaoProcessoDAO.getIdProcessosAbertosByIdTarefa(getSelected(), getTipoProcesso(), isTabComunicacoesExpedidas());
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
        r.setParameter("id", selected.get("idCaixa"));
        r.execute();
    }
    
    public Long getTaskId() {
        if (selected != null) {
            return selected.get("idTask", Long.class);
        }
        return null;
    }

    public Integer getTarefaId() {
        if (selected != null) {
            return selected.get("idTarefa", Integer.class);
        }
        return null;
    }

    public void setSelected(Tuple selected) {
        this.selected = selected;
    }

    public Tuple getSelected() {
        return selected;
    }

    public void refresh() {
        painelTreeHandler.refresh();
    }

    private void updateDatatable() {
        List<Integer> idsProcesso = getProcessoIdList();
        if (idsProcesso != null && !idsProcesso.isEmpty()) {
            Integer idProcesso = idsProcesso.get(0);
            List<DefinicaoVariavelProcesso> definicoes = definicaoVariavelProcessoManager.getDefinicaoVariavelProcessoVisivelPainel(idProcesso);
            dynamicColumns = new ArrayList<>();
            for (DefinicaoVariavelProcesso definicao : definicoes) {
                DynamicColumnModel columnModel = new DynamicColumnModel(definicao.getLabel(), String.format(DYNAMIC_COLUMN_EXPRESSION, definicao.getNome()));
                dynamicColumns.add(columnModel);
            }
        }
    }

    public VariavelProcesso getVariavelProcesso(Processo processo, String nome) {
        return variavelProcessoService.getVariavelProcesso(processo, nome);
    }

    public List<DynamicColumnModel> getDynamicColumns() {
        if (dynamicColumns == null) {
            updateDatatable();
        }
        return dynamicColumns;
    }
    
    @Override
    public void setTab(String tab) {
    	boolean changed = !Objects.equals(tab, getTab());
    	super.setTab(tab);
    	if (changed) onTabChange();
    }

	protected TipoProcesso getTipoProcesso() {
		return tipoProcesso;
	}

	protected void setTipoProcesso(TipoProcesso tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

    public String getTabComunicacaoEletronica() {
        return tabComunicacaoEletronica;
    }

    public void setTabComunicacaoEletronica(String tabComunicacaoEletronica) {
        boolean changed = !Objects.equals(tabComunicacaoEletronica, getTabComunicacaoEletronica());
        this.tabComunicacaoEletronica = tabComunicacaoEletronica;
        if (changed) onTabChange();
    }
    
    public Boolean isTabComunicacoesExpedidas() {
        return (getTabComunicacaoEletronica() != null && "tabExpedidas".equals(getTabComunicacaoEletronica())) ? true : false;
    }
    
    public String getDestinatarioComunicacao(Processo processo) {
        DestinatarioModeloComunicacao destinatario = processo.getMetadado(ComunicacaoMetadadoProvider.DESTINATARIO).getValue();
        return destinatario.getNome();
    }
}
