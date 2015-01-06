package br.com.infox.epp.painel;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Tuple;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.Redirect;
import org.richfaces.event.DropEvent;

import br.com.infox.componentes.column.DynamicColumnModel;
import br.com.infox.core.action.ActionMessagesService;
import br.com.infox.core.controller.AbstractController;
import br.com.infox.core.persistence.DAOException;
import br.com.infox.epp.fluxo.entity.DefinicaoVariavelProcesso;
import br.com.infox.epp.fluxo.entity.Fluxo;
import br.com.infox.epp.fluxo.manager.DefinicaoVariavelProcessoManager;
import br.com.infox.epp.painel.caixa.Caixa;
import br.com.infox.epp.painel.caixa.CaixaManager;
import br.com.infox.epp.processo.consulta.list.ConsultaProcessoList;
import br.com.infox.epp.processo.entity.Processo;
import br.com.infox.epp.processo.manager.ProcessoManager;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.epp.processo.variavel.bean.VariavelProcesso;
import br.com.infox.epp.processo.variavel.service.VariavelProcessoService;
import br.com.infox.epp.tarefa.component.tree.TarefasTreeHandler;

@Name(PainelUsuarioController.NAME)
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings(UNCHECKED)
public class PainelUsuarioController extends AbstractController {
	
    private static final long serialVersionUID = 1L;

    public static final String NAME = "painelUsuarioController";
    private static final String DYNAMIC_COLUMN_EXPRESSION = "#{painelUsuarioController.getVariavelProcesso(row, '%s').valor}";

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
    
    private Tuple selected;
    private List<Integer> processoIdList;
    private List<DynamicColumnModel> dynamicColumns;
    private TipoProcesso tipoProcesso;

    @Observer("selectedTarefasTree")
    public void onSelected(Object obj) {
        setSelected((Tuple) obj);
        processoIdList = null;
        dynamicColumns = null;
        updateDatatable();
    }
    
    private void onTabChange() {
    	processoIdList = null;
    	selected = null;
    	setTipoProcesso();
		TarefasTreeHandler.instance().setTipoProcesso(getTipoProcesso());
    }

    /**
     * Método protected para poder ser sobrescrito pelos módulos do epp
     * */
	protected void setTipoProcesso() {
		setTipoProcesso(TipoProcesso.getByName(getTab()));
	}

    public Integer getIdCaixa() {
        if ("caixa".equalsIgnoreCase(getSelectedType())) {
            return selected.get("idCaixa", Integer.class);
        }
        return null;
    }
    
    private String getSelectedType(){
    	return selected != null ? selected.get("type", String.class) : null;
    }

    public List<Integer> getProcessoIdList() {
        if (selected != null && !"fluxo".equalsIgnoreCase(getSelectedType())) {
            if (processoIdList == null) {
                processoIdList = situacaoProcessoDAO.getProcessosAbertosByIdTarefa(getTarefaId(), getSelected(), getTipoProcesso());
            }
            if (processoIdList.size() == 0) {
                processoIdList.add(-1);
            }
            return processoIdList;
        }
        return null;
    }

    public void processoCaixa(DropEvent evt) {
        Caixa caixa = caixaManager.find((Integer) evt.getDropValue());
        setProcessoCaixa(getProcessoIdList(evt.getDragValue()), caixa);
        this.processoIdList = null;
    }

    public void setProcessoCaixa(List<Integer> idList, Caixa caixa) {
        try {
            processoManager.moverProcessosParaCaixa(idList, caixa);
            refresh();
        } catch (DAOException e) {
            actionMessagesService.handleDAOException(e);
        }
    }

    private List<Integer> getProcessoIdList(Object o) {
        List<Integer> list = new ArrayList<Integer>();
        if (o instanceof Processo) {
            list.add(((Processo) o).getIdProcesso());
        } else if (o instanceof List) {
            List<Processo> processoList = (List<Processo>) o;
            for (Processo cpt : processoList) {
                list.add(cpt.getIdProcesso());
            }
        }
        return list;
    }

    public void processoCaixaTarefa(DropEvent evt) {
        setProcessoCaixa(getProcessoIdList(evt.getDragValue()), null);
    }

    public Long getTaskId() {
        if (selected != null) {
            return (Long) selected.get("idTask");
        }
        return null;
    }

    public Integer getTarefaId() {
        if (selected != null) {
            return (Integer) selected.get("idTarefa");
        }
        return null;
    }

    public void setSelected(Tuple selected) {
        this.selected = selected;
    }

    public Tuple getSelected() {
        return selected;
    }

    public void editaCaixa() {
        Redirect r = new Redirect();
        r.setViewId("/Caixa/listView.xhtml");
        r.setParameter("tab", "form");
        r.setParameter("id", selected.get("idCaixa"));
        r.execute();
    }

    public void refresh() {
        TarefasTreeHandler.instance().refresh();
    }

    public static PainelUsuarioController instance() {
        return (PainelUsuarioController) Contexts.getConversationContext().get(NAME);
    }

    public void setProcessoCaixa(Caixa caixa) {
        if (consultaProcessoList.getResultCount() > 0) {
            List<Integer> idList = getProcessoIdList(consultaProcessoList.getResultList());
            setProcessoCaixa(idList, caixa);
            refresh();
            processoIdList = null;
        }
    }

    private void updateDatatable() {
        List<Integer> idsProcesso = getProcessoIdList();

        if (idsProcesso != null
                && (idsProcesso.size() > 1 || (idsProcesso.size() == 1 && idsProcesso.get(0) != -1))) {
            Processo processo = processoManager.find(idsProcesso.get(0));
            Fluxo fluxo = processo.getNaturezaCategoriaFluxo().getFluxo();

            List<DefinicaoVariavelProcesso> definicoes = definicaoVariavelProcessoManager.listVariaveisByFluxo(fluxo);
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
        if (this.dynamicColumns == null) {
            updateDatatable();
        }
        return this.dynamicColumns;
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
}
