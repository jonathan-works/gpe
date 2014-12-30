package br.com.infox.epp.tarefa.component.tree;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;

import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.processo.situacao.service.SituacaoProcessoService;
import br.com.infox.epp.processo.type.TipoProcesso;
import br.com.infox.seam.util.ComponentUtil;

public class PainelEntityNode extends EntityNode<Tuple> {

    private static final long serialVersionUID = 1L;
    private List<PainelEntityNode> rootNodes;
    private List<PainelEntityNode> nodes;
    private List<EntityNode<Tuple>> caixas;
    private List<TypedQuery<Tuple>> queryCaixas = new ArrayList<>();
    private TipoProcesso tipoProcesso;

    public PainelEntityNode(List<TypedQuery<Tuple>> queryCaixas, TipoProcesso tipoProcesso) {
        super("");
        this.queryCaixas = queryCaixas;
        this.tipoProcesso = tipoProcesso;
    }

    public PainelEntityNode(EntityNode<Tuple> parent, Tuple entity, List<TypedQuery<Tuple>> queryCaixas, TipoProcesso tipoProcesso) {
        super(parent, entity, new String[0]);
        this.queryCaixas = queryCaixas;
        this.tipoProcesso = tipoProcesso;
    }

    public List<EntityNode<Tuple>> getCaixas() {
        if (caixas == null) {
            caixas = new ArrayList<EntityNode<Tuple>>();
            boolean parent = true;
            for (TypedQuery<Tuple> typedQuery : queryCaixas) {
                if (!isLeaf()) {
                    List<Tuple> children = getCaixasList(typedQuery, getEntity());
                    for (Tuple entity : children) {
                        if (!entity.equals(getIgnore())) {
                            EntityNode<Tuple> node = createChildNode(entity);
                            node.setIgnore(getIgnore());
                            node.setLeaf(!parent);
                            caixas.add(node);
                        }
                    }
                    parent = false;
                }
            }
        }
        return caixas;
    }

	public List<PainelEntityNode> getRootsFluxos() {
        if (rootNodes == null) {
            rootNodes = new ArrayList<>();
            List<Tuple> roots = getSituacaoProcessoService().getRootList(tipoProcesso);
            for (Tuple entity : roots) {
                if (!entity.equals(getIgnore())) {
                    PainelEntityNode node = createRootNode(entity);
                    node.setIgnore(getIgnore());
                    rootNodes.add(node);
                }
            }
        }
        return rootNodes;
    }

    public List<PainelEntityNode> getNodesTarefas() {
        if (nodes == null) {
            nodes = new ArrayList<>();
            boolean parent = true;
            if (!isLeaf()) {
            	Integer idFluxo = getEntity().get("idFluxo", Integer.class);
                List<Tuple> children = getSituacaoProcessoService().getChildrenList(idFluxo, tipoProcesso);
                for (Tuple entity : children) {
                    if (!entity.equals(getIgnore())) {
                        PainelEntityNode node = createChildNode(entity);
                        node.setIgnore(getIgnore());
                        node.setLeaf(!parent);
                        nodes.add(node);
                    }
                }
                parent = false;
            }
        }
        return nodes;
    }

    @Override
    protected PainelEntityNode createRootNode(Tuple entity) {
        return new PainelEntityNode(null, entity, queryCaixas, tipoProcesso);
    }

    protected List<Tuple> getCaixasList(TypedQuery<Tuple> typedQuery , Tuple entity) {
        return typedQuery.setParameter("taskId", entity.get("idTarefa")).getResultList();
    }

    @Override
    public String getType() {
        return getEntity().get("type", String.class);
    }

    @Override
    protected PainelEntityNode createChildNode(Tuple entity) {
        return new PainelEntityNode(this, entity, queryCaixas, tipoProcesso);
    }

    public Integer getTarefaId() {
    	Integer idTarefa = getEntity().get("idTarefa", Integer.class); 
        return idTarefa == null ? 0 : idTarefa;
    }

    public Integer getTaskId() {
    	Integer idTask = getEntity().get("idTask", Integer.class);
        return idTask == null ? 0 : idTask;
    }

    private SituacaoProcessoService getSituacaoProcessoService() {
        return ComponentUtil.getComponent(SituacaoProcessoService.NAME);
    }
    
}
