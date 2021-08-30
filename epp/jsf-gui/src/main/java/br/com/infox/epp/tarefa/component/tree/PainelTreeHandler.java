package br.com.infox.epp.tarefa.component.tree;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.richfaces.event.TreeSelectionChangeEvent;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.epp.cdi.ViewScoped;
import br.com.infox.epp.painel.FluxoBean;
import br.com.infox.epp.painel.PainelUsuarioController;
import br.com.infox.epp.painel.TaskDefinitionBean;

@Named
@ViewScoped
public class PainelTreeHandler extends AbstractTreeHandler<TaskDefinitionBean> {

    private static final long serialVersionUID = 1L;

    @Inject
    private PainelUsuarioController painelUsuarioController;

    private List<PainelEntityNode> rootList;
    private FluxoBean fluxoBean;

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

    public List<PainelEntityNode> getTarefasRoots() {
        if (rootList == null) {
            rootList = new ArrayList<>();
            for (TaskDefinitionBean taskDefinitionBean : fluxoBean.getTaskDefinitions().values()) {
                rootList.add(new PainelEntityNode(null, taskDefinitionBean, PainelEntityNode.TASK_TYPE));
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

	public FluxoBean getFluxoBean() {
		return fluxoBean;
	}

	public void setFluxoBean(FluxoBean fluxoBean) {
		this.fluxoBean = fluxoBean;
	}

}
