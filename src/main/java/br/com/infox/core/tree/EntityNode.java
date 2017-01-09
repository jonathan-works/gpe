package br.com.infox.core.tree;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.core.util.ArrayUtil;
import br.com.infox.seam.util.ComponentUtil;

public class EntityNode<E> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String PARENT_NODE = "parent";
    private E entity;
    private E ignore;
    private boolean leaf;

    private String[] queryChildrenList;

    private List<EntityNode<E>> rootNodes;
    private List<EntityNode<E>> nodes;
    // Variavel para adição da selectBooleanCheckBox
    private Boolean selected = false;
    private EntityNode<E> parent;

    /**
     * @param queryChildren query que retorna os nós filhos da entidade
     *        selecionada
     */
    public EntityNode(String queryChildren) {
        this.queryChildrenList = new String[] { queryChildren };
    }

    public EntityNode(String[] queryChildrenList) {
        this.queryChildrenList = ArrayUtil.copyOf(queryChildrenList);
    }

    public EntityNode(EntityNode<E> parent, E entity, String[] queryChildrenList) {
        this.queryChildrenList = ArrayUtil.copyOf(queryChildrenList);
        this.parent = parent;
        this.entity = entity;
    }

    /**
     * Busca os nós filhos. Dispara um evento entityNodesPostGetNodes
     * 
     * @return lista de nós filhos da entidade passada no construtor
     */
    public List<EntityNode<E>> getNodes() {
        if (nodes == null) {
            nodes = new ArrayList<EntityNode<E>>();
            boolean parent = true;
            if (queryChildrenList != null) {
                for (String query : queryChildrenList) {
                    if (!isLeaf()) {
                        List<E> children = getChildrenList(query, entity);
                        for (E n : children) {
                            if (!n.equals(ignore)) {
                                EntityNode<E> node = createChildNode(n);
                                node.setIgnore(ignore);
                                node.setLeaf(!parent);
                                nodes.add(node);
                            }
                        }
                        parent = false;
                    }
                }
            }
        }
        return nodes;
    }

    protected String[] getQueryChildrenList() {
        return ArrayUtil.copyOf(queryChildrenList);
    }

    @SuppressWarnings(UNCHECKED)
    protected List<E> getChildrenList(String hql, E entity) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put(PARENT_NODE, entity);
        GenericDAO genericDAO = ComponentUtil.getComponent(GenericDAO.NAME);
        return (List<E>) genericDAO.getResultList(hql, parameters);
    }

    protected EntityNode<E> createChildNode(E n) {
        return new EntityNode<E>(this, n, this.queryChildrenList);
    }

    protected EntityNode<E> createRootNode(E n) {
        return new EntityNode<E>(null, n, this.queryChildrenList);
    }

    /**
     * 
     * @return a entidade representada pelo nó
     */
    public E getEntity() {
        return entity;
    }

    /**
     * 
     * @param queryRoots query que retorna os nós do primeiro nível
     * @return lista dos nós do primeiro nível
     */
    public List<EntityNode<E>> getRoots(Query queryRoots) {
        if (rootNodes == null) {
            rootNodes = new ArrayList<EntityNode<E>>();
            @SuppressWarnings(UNCHECKED) List<E> roots = (List<E>) queryRoots.getResultList();
            for (E e : roots) {
                if (!e.equals(ignore)) {
                    EntityNode<E> node = createRootNode(e);
                    node.setIgnore(ignore);
                    rootNodes.add(node);
                }
            }
        }
        return rootNodes;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
    }

    public String getType() {
        return isLeaf() ? "leaf" : "folder";
    }

    @Override
    public String toString() {
        return entity.toString();
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public Boolean getSelected() {
        return selected;
    }

    /**
     * Metodo que adiciona a entidade que deve ser ignorada na composição da
     * tree
     * 
     * @param ignore
     */
    public void setIgnore(E ignore) {
        this.ignore = ignore;
    }

    public E getIgnore() {
        return ignore;
    }

    public EntityNode<E> getParent() {
        return parent;
    }
    
    public void setParent(EntityNode<E> entityNode) {
        this.parent = entityNode;
    }

    public boolean canSelect() {
        return true;
    }

    public String[] getQueryChildren() {
        return ArrayUtil.copyOf(queryChildrenList);
    }
}
