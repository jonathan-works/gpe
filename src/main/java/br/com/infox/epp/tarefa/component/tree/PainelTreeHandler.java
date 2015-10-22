package br.com.infox.epp.tarefa.component.tree;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Tuple;

import org.richfaces.event.TreeSelectionChangeEvent;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.painel.FluxoBean;
import br.com.infox.epp.painel.PainelUsuarioController;
import br.com.infox.epp.processo.situacao.manager.SituacaoProcessoManager;
import br.com.infox.epp.processo.type.TipoProcesso;

@Named
@ViewScoped
public class PainelTreeHandler extends AbstractTreeHandler<Tuple> {

    public static final String NAME = "painelTreeHandler"; 
    private static final long serialVersionUID = 1L;
    
    @Inject
    private PainelUsuarioController painelUsuarioController;
    @Inject
    private SituacaoProcessoManager situacaoProcessoManager;
    
    private List<PainelEntityNode> rootList;
    private FluxoBean fluxoBean;
    private String numeroProcessoRoot;

    @Override
    protected String getQueryRoots() {
        throw new IllegalStateException("Usar SituacaoProcessoDAO::createQueryRoots ao invés de TarefasTreeHanlder::getQueryRoots");
    }

    @Override
    protected String getQueryChildren() {
        throw new IllegalStateException("Usar SituacaoProcessoDAO::createQueryChildren ao invés de TarefasTreeHanlder::getQueryChildren");
    }

    @Override
    protected String getEventSelected() {
        return "selectedTarefasTree";
    }
    
    public void processTreeSelectionChange(TreeSelectionChangeEvent ev) {
    	super.processTreeSelectionChange(ev);
		painelUsuarioController.onSelectNode();
    }

    public Integer getTaskId() {
        if (getSelected() != null) {
            return getSelected().get("idTask", Integer.class);
        }
        return 0;
    }
    
    public Integer getTarefaId(){
    	if (getSelected() != null) {
    		return getSelected().get("idTarefa", Integer.class);
    	}
    	return null;
    }
    
    public List<PainelEntityNode> getTarefasRoots() {
        if (rootList == null || rootList.isEmpty()) {
        	List<Tuple> tuples = situacaoProcessoManager.getChildrenList(getIdFluxo(), getTipoProcesso(), isExpedidas(), getNumeroProcessoRoot());
        	rootList = new ArrayList<>(tuples.size());
            for (Tuple tuple : tuples) {
            	rootList.add(new PainelEntityNode(null, tuple, getTipoProcesso(), isExpedidas(), getNumeroProcessoRoot()));
            }
        }
        return rootList;
    }

    public void refresh() {
        if (rootList != null) {
            rootList.clear();
        }
    }

    @Override
    public void clearTree() {
        rootList = null;
        super.clearTree();
    }

	public TipoProcesso getTipoProcesso() {
		return fluxoBean.getTipoProcesso();
	}

	public boolean isExpedidas() {
		return fluxoBean.getExpedida();
	}

	public Integer getIdFluxo() {
		return Integer.valueOf(fluxoBean.getProcessDefinitionId());
	}

	public FluxoBean getFluxoBean() {
		return fluxoBean;
	}

	public void setFluxoBean(FluxoBean fluxoBean) {
		this.fluxoBean = fluxoBean;
	}

	public String getNumeroProcessoRoot() {
		return numeroProcessoRoot;
	}

	public void setNumeroProcessoRoot(String numeroProcessoRoot) {
		this.numeroProcessoRoot = numeroProcessoRoot;
	}
	
}
