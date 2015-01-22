package br.com.infox.epp.tarefa.component.tree;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.seam.util.ComponentUtil;

public class PainelEntityNode extends EntityNode<Tuple> {
	
    private static final long serialVersionUID = 1L;
    public static final String FLUXO_TYPE = "Fluxo";
    public static final String TASK_TYPE = "Task";
    public static final String CAIXA_TYPE = "Caixa";
    
    private List<PainelEntityNode> rootNodes;
    private List<PainelEntityNode> nodes;
    private List<EntityNode<Tuple>> caixas;
    private TipoProcesso tipoProcesso;

    public PainelEntityNode(TipoProcesso tipoProcesso) {
        super("");
        this.tipoProcesso = tipoProcesso;
    }

    public PainelEntityNode(EntityNode<Tuple> parent, Tuple entity, TipoProcesso tipoProcesso) {
        super(parent, entity, new String[0]);
        this.tipoProcesso = tipoProcesso;
    }

    public List<EntityNode<Tuple>> getCaixas() {
        if (caixas == null) {
            caixas = new ArrayList<EntityNode<Tuple>>();
            if (!isLeaf()) {
            	List<Tuple> children = getSituacaoProcessoDAO().getCaixaList(tipoProcesso, getTarefaId());
        		for (Tuple entity : children) {
        		    caixas.add(new PainelEntityNode(this, entity, tipoProcesso));
        		}
            }
        }
        return caixas;
    }

	public List<PainelEntityNode> getRootsFluxos() {
        if (rootNodes == null) {
            rootNodes = new ArrayList<>();
            List<Tuple> roots = getSituacaoProcessoDAO().getRootList(tipoProcesso);
            for (Tuple entity : roots) {
            	rootNodes.add(new PainelEntityNode(null, entity, tipoProcesso));
            }
        }
        return rootNodes;
    }

    public List<PainelEntityNode> getNodesTarefas() {
        if (nodes == null) {
            nodes = new ArrayList<>();
            if (!isLeaf()) {
            	Integer idFluxo = getEntity().get("idFluxo", Integer.class);
        		List<Tuple> children = getSituacaoProcessoDAO().getChildrenList(idFluxo, tipoProcesso);
        		for (Tuple entity : children) {
        		    nodes.add(new PainelEntityNode(this, entity, tipoProcesso));
        		}
            }
        }
        return nodes;
    }

    @Override
    public String getType() {
        return getEntity().get("type", String.class);
    }

    public Integer getTarefaId() {
    	Integer idTarefa = getEntity().get("idTarefa", Integer.class); 
        return idTarefa == null ? 0 : idTarefa;
    }

    public Long getTaskId() {
    	Long idTask = getEntity().get("idTask", Long.class);
        return idTask == null ? 0L : idTask;
    }

    private SituacaoProcessoDAO getSituacaoProcessoDAO() {
        return ComponentUtil.getComponent(SituacaoProcessoDAO.NAME);
    }
    
}
