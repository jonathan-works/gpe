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
    private Boolean comunicacoesExpedidas;

    public PainelEntityNode(TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas) {
        super("");
        this.tipoProcesso = tipoProcesso;
        this.comunicacoesExpedidas = comunicacoesExpedidas;
    }
    
    public PainelEntityNode(EntityNode<Tuple> parent, Tuple entity, TipoProcesso tipoProcesso, Boolean comunicacoesExpedidas) {
        super(parent, entity, new String[0]);
        this.tipoProcesso = tipoProcesso;
        this.comunicacoesExpedidas = comunicacoesExpedidas;
    }

    public List<EntityNode<Tuple>> getCaixas() {
        if (caixas == null) {
            caixas = new ArrayList<EntityNode<Tuple>>();
            if (!isLeaf()) {
            	List<Tuple> children = getSituacaoProcessoDAO().getCaixaList(tipoProcesso, getTarefaId(), comunicacoesExpedidas);
        		for (Tuple entity : children) {
        		    caixas.add(new PainelEntityNode(this, entity, tipoProcesso, comunicacoesExpedidas));
        		}
            }
        }
        return caixas;
    }

	public List<PainelEntityNode> getRootsFluxos() {
        if (rootNodes == null) {
            rootNodes = new ArrayList<>();
            List<Tuple> roots = getSituacaoProcessoDAO().getRootList(tipoProcesso, comunicacoesExpedidas);
            for (Tuple entity : roots) {
            	rootNodes.add(new PainelEntityNode(null, entity, tipoProcesso, comunicacoesExpedidas));
            }
        }
        return rootNodes;
    }

    public List<PainelEntityNode> getNodesTarefas() {
        if (nodes == null) {
            nodes = new ArrayList<>();
            if (!isLeaf()) {
            	Integer idFluxo = getEntity().get("idFluxo", Integer.class);
        		List<Tuple> children = getSituacaoProcessoDAO().getChildrenList(idFluxo, tipoProcesso, comunicacoesExpedidas);
        		for (Tuple entity : children) {
        		    nodes.add(new PainelEntityNode(this, entity, tipoProcesso, comunicacoesExpedidas));
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
