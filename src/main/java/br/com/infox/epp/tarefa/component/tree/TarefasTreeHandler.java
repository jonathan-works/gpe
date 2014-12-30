package br.com.infox.epp.tarefa.component.tree;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.epp.processo.situacao.service.SituacaoProcessoService;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.seam.util.ComponentUtil;

@BypassInterceptors
@Name(TarefasTreeHandler.NAME)
@Install(precedence = Install.FRAMEWORK)
public class TarefasTreeHandler extends AbstractTreeHandler<Tuple> {

    public static final String NAME = "tarefasTree";
    private static final long serialVersionUID = 1L;
    private List<PainelEntityNode> rootList;
    private TipoProcesso tipoProcesso;

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
            return getSelected().get("idTask", Integer.class);
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
    protected PainelEntityNode createNode() {
        return new PainelEntityNode(getQueryCaixasList(), getTipoProcesso());
    }

    public List<PainelEntityNode> getTarefasRoots() {
        if (rootList == null || rootList.isEmpty()) {
            PainelEntityNode entityNode = createNode();
            rootList = entityNode.getRootsFluxos();
        }
        return rootList;
    }

    public void refresh() {
        if (rootList != null) {
            rootList.clear();
        }
    }

    private List<TypedQuery<Tuple>> getQueryCaixasList() {
        List<TypedQuery<Tuple>> list = new ArrayList<>();
        list.add(getSituacaoProcessoService().createQueryCaixas(getTipoProcesso()));
        return list;
    }

    @Override
    public void clearTree() {
        rootList = null;
        super.clearTree();
    }

    private SituacaoProcessoService getSituacaoProcessoService() {
        return ComponentUtil.getComponent(SituacaoProcessoService.NAME);
    }

	public TipoProcesso getTipoProcesso() {
		return tipoProcesso;
	}

	public void setTipoProcesso(TipoProcesso tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

}
