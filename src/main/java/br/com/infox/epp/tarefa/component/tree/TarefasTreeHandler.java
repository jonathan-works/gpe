package br.com.infox.epp.tarefa.component.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.core.Events;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.seam.util.ComponentUtil;

@Name(TarefasTreeHandler.NAME)
@Install(precedence = Install.FRAMEWORK)
@BypassInterceptors
public class TarefasTreeHandler extends AbstractTreeHandler<Map<String, Object>> {

    public static final String NAME = "tarefasTree";
    public static final String FILTER_TAREFAS_TREE = "br.com.infox.ibpm.component.tree.FilterTarefasTree";
    public static final String CLEAR_TAREFAS_TREE_EVENT = "clearTarefasTreeEvent";
    private static final long serialVersionUID = 1L;
    private List<TarefasEntityNode<Map<String, Object>>> rootList;
    private String tipoProcesso;

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

    public Integer getTaskId() {
        if (getSelected() != null) {
            return (Integer) getSelected().get("idTask");
        }
        return 0;
    }

    public static TarefasTreeHandler instance() {
        return (TarefasTreeHandler) Component.getInstance(TarefasTreeHandler.NAME);
    }

    public static void clearActiveTree() {
        ((TarefasTreeHandler) Component.getInstance(TarefasTreeHandler.NAME)).clearTree();
    }

    @Override
    protected TarefasEntityNode<Map<String, Object>> createNode() {
        return new TarefasEntityNode<Map<String, Object>>(getQueryCaixasList(), getTipoProcesso());
    }

    public List<TarefasEntityNode<Map<String, Object>>> getTarefasRoots() {
        if (rootList == null || rootList.isEmpty()) {
            Events.instance().raiseEvent(FILTER_TAREFAS_TREE);
            TarefasEntityNode<Map<String, Object>> entityNode = createNode();
            rootList = entityNode.getRootsFluxos();
        }
        return rootList;
    }

    public void refresh() {
        if (rootList != null) {
            rootList.clear();
        }
    }

    private List<Query> getQueryCaixasList() {
        List<Query> list = new ArrayList<Query>();
        list.add(getSituacaoProcessoDAO().createQueryCaixas());
        return list;
    }

    @Override
    public void clearTree() {
        Events.instance().raiseEvent(CLEAR_TAREFAS_TREE_EVENT);
        rootList = null;
        super.clearTree();
    }

    private SituacaoProcessoDAO getSituacaoProcessoDAO() {
        return ComponentUtil.getComponent(SituacaoProcessoDAO.NAME);
    }

	public String getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(String tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}
    
}
