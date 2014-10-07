package br.com.infox.epp.tarefa.component.tree;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.jboss.seam.core.Events;

import br.com.infox.core.tree.EntityNode;
import br.com.infox.epp.processo.situacao.dao.SituacaoProcessoDAO;
import br.com.infox.seam.util.ComponentUtil;

public class TarefasEntityNode<E> extends EntityNode<Map<String, Object>> {

    private static final long serialVersionUID = 1L;
    private List<TarefasEntityNode<E>> rootNodes;
    private List<TarefasEntityNode<E>> nodes;
    private List<EntityNode<E>> caixas;
    private List<Query> queryCaixas = new ArrayList<Query>();

    public TarefasEntityNode(List<Query> queryCaixas) {
        super("");
        this.queryCaixas = queryCaixas;
    }

    public TarefasEntityNode(EntityNode<Map<String, Object>> parent, Map<String, Object> entity, List<Query> queryCaixas) {
        super(parent, entity, new String[0]);
        this.queryCaixas = queryCaixas;
    }

    @SuppressWarnings(UNCHECKED)
    public List<EntityNode<E>> getCaixas() {
        if (caixas == null) {
            caixas = new ArrayList<EntityNode<E>>();
            boolean parent = true;
            for (Query query : queryCaixas) {
                if (!isLeaf()) {
                    List<E> children = (List<E>) getCaixasList(query, getEntity());
                    for (E n : children) {
                        if (!n.equals(getIgnore())) {
                            EntityNode<E> node = (EntityNode<E>) createChildNode((Map<String, Object>) n);
                            node.setIgnore((E) getIgnore());
                            node.setLeaf(!parent);
                            caixas.add(node);
                        }
                    }
                    parent = false;
                }
            }

            Events.instance().raiseEvent("entityNodesPostGetNodes", caixas);
        }
        return caixas;
    }

    @SuppressWarnings(UNCHECKED)
    public List<TarefasEntityNode<E>> getRootsFluxos(Query queryRoots) {
        if (rootNodes == null) {
            rootNodes = new ArrayList<TarefasEntityNode<E>>();
            List<E> roots = queryRoots.getResultList();
            for (E e : roots) {
                if (!e.equals(getIgnore())) {
                    TarefasEntityNode<Map<String, Object>> node = createRootNode((Map<String, Object>) e);
                    node.setIgnore(getIgnore());
                    rootNodes.add((TarefasEntityNode<E>) node);
                }
            }
        }
        return rootNodes;
    }

    @SuppressWarnings(UNCHECKED)
    public List<TarefasEntityNode<E>> getNodesTarefas() {
        if (nodes == null) {
            nodes = new ArrayList<TarefasEntityNode<E>>();
            boolean parent = true;
                if (!isLeaf()) {
                    Query query = getSituacaoProcessoDAO().createQueryChildren((Integer) getEntity().get("idFluxo"));
                    List<E> children = (List<E>) query.getResultList();
                    for (E n : children) {
                        if (!n.equals(getIgnore())) {
                            TarefasEntityNode<Map<String, Object>> node = createChildNode((Map<String, Object>) n);
                            node.setIgnore(getIgnore());
                            node.setLeaf(!parent);
                            nodes.add((TarefasEntityNode<E>) node);
                        }
                    }
                    parent = false;
                }

            Events.instance().raiseEvent("entityNodesPostGetNodes", nodes);
        }
        return nodes;
    }

    @Override
    protected TarefasEntityNode<Map<String, Object>> createRootNode(
            Map<String, Object> n) {
        return new TarefasEntityNode<Map<String, Object>>(null, n, queryCaixas);
    }

    @SuppressWarnings(UNCHECKED)
    protected List<Map<String, Object>> getCaixasList(Query query,
            Map<String, Object> entity) {
        return query.setParameter("taskId", entity.get("idTarefa")).getResultList();
    }

    @Override
    public String getType() {
        return (String) getEntity().get("type");
    }

    @Override
    protected TarefasEntityNode<Map<String, Object>> createChildNode(
            Map<String, Object> n) {
        return new TarefasEntityNode<Map<String, Object>>(this, n, queryCaixas);
    }

    public Integer getTarefaId() {
        if (getEntity() != null) {
            return (Integer) getEntity().get("idTarefa");
        }
        return 0;
    }

    public Integer getTaskId() {
        if (getEntity() != null) {
            return ((Long) getEntity().get("idTask")).intValue();
        }
        return 0;
    }

    private SituacaoProcessoDAO getSituacaoProcessoDAO() {
        return ComponentUtil.getComponent(SituacaoProcessoDAO.NAME);
    }

}
