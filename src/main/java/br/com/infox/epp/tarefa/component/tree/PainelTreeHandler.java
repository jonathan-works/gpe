package br.com.infox.epp.tarefa.component.tree;

import java.util.List;

import javax.persistence.Tuple;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import br.com.infox.core.tree.AbstractTreeHandler;
import br.com.infox.epp.processo.type.TipoProcesso;

@AutoCreate
@Name(PainelTreeHandler.NAME)
public class PainelTreeHandler extends AbstractTreeHandler<Tuple> {

    public static final String NAME = "painelTreeHandler"; 
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

    public List<PainelEntityNode> getTarefasRoots() {
        if (rootList == null || rootList.isEmpty()) {
            PainelEntityNode entityNode = new PainelEntityNode(getTipoProcesso());
            rootList = entityNode.getRootsFluxos();
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
		return tipoProcesso;
	}

	public void setTipoProcesso(TipoProcesso tipoProcesso) {
		this.tipoProcesso = tipoProcesso;
	}

}
