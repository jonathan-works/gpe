package br.com.infox.core.tree;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.persistence.Query;

import org.apache.commons.lang3.time.StopWatch;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.richfaces.component.UICollapsiblePanel;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import br.com.infox.core.dao.GenericDAO;
import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

@Scope(ScopeType.PAGE)
public abstract class AbstractTreeHandler<E> implements TreeHandler<E>, Serializable {

    private static final int LIMITE_VISUALIZACAO = 25;
    private static final LogProvider LOG = Logging.getLogProvider(AbstractTreeHandler.class);
    private static final long serialVersionUID = 1L;

    private E selected;
    private List<EntityNode<E>> rootList;
    private String treeId;
    private String iconFolder;
    private String iconLeaf;
    private boolean folderSelectable = true;
    private String expression;
    private List<EntityNode<E>> selectedNodesList = new ArrayList<EntityNode<E>>(0);

    @Override
    public void clearTree() {
        selectedNodesList = new ArrayList<EntityNode<E>>();
        rootList = null;
        selected = null;
        clearUITree();
        if (expression != null) {
            Expressions.instance().createValueExpression(expression).setValue(null);
        }
    }

    private void clearUITree() {
        if (treeId != null) {
            UITree tree = (UITree) FacesContext.getCurrentInstance().getViewRoot().findComponent(treeId);
            if (tree != null) {
            	 tree.setRowKey(null);
                 tree.setSelection(null);
                 closeParentPanel(tree);
            }
        }
    }

    @Override
    public List<EntityNode<E>> getRoots() {
        if (rootList == null) {
            StopWatch sw = new StopWatch();
            sw.start();
            Query queryRoots = genericDAO().createQuery(getQueryRoots(), null);
            EntityNode<E> entityNode = createNode();
            entityNode.setIgnore(getEntityToIgnore());
            rootList = entityNode.getRoots(queryRoots);
            LOG.info(".getRoots(): " + sw.getTime());
        }
        return rootList;
    }

    protected EntityNode<E> createNode() {
        return new EntityNode<E>(getQueryChildrenList());
    }

    /**
     * Lista de queries que irão gerar os nós filhos Caso haja mais de uma
     * query, deve-se sobrescrever esse método e retornar null no método
     * getQueryChildren()
     * 
     * @return
     */
    protected String[] getQueryChildrenList() {
        String[] children = new String[1];
        children[0] = getQueryChildren();
        return children;
    }

    @Override
    @SuppressWarnings(UNCHECKED)
    public E getSelected() {
        if (expression == null) {
            return selected;
        }
        Object value = null;
        try {
            value = Expressions.instance().createValueExpression(expression).getValue();
        } catch (Exception ignore) {
            LOG.error(".getSelected()", ignore);
        }
        return (E) value;
    }

    @Override
    public void setSelected(E selected) {
        if (expression == null) {
            this.selected = selected;
        } else {
            Expressions.instance().createValueExpression(expression).setValue(selected);
        }
    }

    @SuppressWarnings(UNCHECKED)
    @Override
    public void processTreeSelectionChange(TreeSelectionChangeEvent ev) {
        // Considerando single selection
    	Iterator<Object> it = ev.getNewSelection().iterator();
    	if (!it.hasNext()) {
    		return;
    	}
        Object selectionKey = it.next();
        UITree tree = (UITree) ev.getSource();
        treeId = ":" + tree.getClientId();

        Object key = tree.getRowKey();
        tree.setRowKey(selectionKey);
        EntityNode<E> en = (EntityNode<E>) tree.getRowData();
        tree.setRowKey(key);
        setSelected(en.getEntity());
        closeParentPanel(tree);
        raiseEvents(en);
    }

    protected void raiseEvents(EntityNode<E> en) {
        Events.instance().raiseEvent(getEventSelected(), getSelected());
        if (getTreeItemSelect() != null) {
            BeanManager.INSTANCE.fireEvent(getSelected(), getTreeItemSelect());
        }
    }

    protected String getEventSelected() {
        return null;
    }
    
    protected Annotation getTreeItemSelect() {
        return null;
    }

    protected abstract String getQueryRoots();

    protected abstract String getQueryChildren();

    @Override
    public String getIconFolder() {
        return iconFolder;
    }

    @Override
    public void setIconFolder(String iconFolder) {
        this.iconFolder = iconFolder;
    }

    @Override
    public String getIconLeaf() {
        return iconLeaf;
    }

    @Override
    public void setIconLeaf(String iconLeaf) {
        this.iconLeaf = iconLeaf;
    }

    @Override
    public boolean isFolderSelectable() {
        return folderSelectable;
    }

    @Override
    public void setFolderSelectable(boolean folderSelectable) {
        this.folderSelectable = folderSelectable;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = "#{" + expression + "}";
    }

    /**
     * Tratamento para que a string não fique maior que o tamanho do campo
     * 
     * @param selected
     * @return
     */
    public String getSelectedView(E selected) {
        String selecionado = "";
        if (selected == null || selected.toString() == null) {
            return selecionado;
        }
        if (selected.toString().length() > LIMITE_VISUALIZACAO) {
            selecionado = selected.toString().substring(0, LIMITE_VISUALIZACAO)
                    + "...";
        } else {
            selecionado = selected.toString();
        }
        return selecionado;
    }

    /**
     * Método que retorna a lista dos itens selecionados.
     * 
     * @return - Lista dos itens selecionados.
     */
    public List<E> getSelectedTree() {
        List<E> selectedList = new ArrayList<E>();
        for (EntityNode<E> node : selectedNodesList) {
            selectedList.add(node.getEntity());
        }
        return selectedList;
    }

    public List<EntityNode<E>> getSelectedNodesList() {
        return selectedNodesList;
    }

    public void setSelectedNodesList(List<EntityNode<E>> selectedNodesList) {
        this.selectedNodesList = selectedNodesList;
    }

    /**
     * Insere o nó selecionado pela checkBox na lista dos nós selecionados.
     * 
     * @param node - Nó selecionado pelo usuário
     */
    public void setSelectedNode(EntityNode<E> node) {
        if (getSelected() == null || getSelected().toString() == null) {
            setSelected(node.getEntity());
        }
        if (selectedNodesList.contains(node)) {
            selectedNodesList.remove(node);
            selectAllChildren(node, false);
        } else {
            selectedNodesList.add(node);
            selectAllChildren(node, true);
        }
    }

    protected void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    private void selectAllChildren(EntityNode<E> selectedNode, boolean operation) {
        for (EntityNode<E> node : selectedNode.getNodes()) {
            selectAllChildren(node, operation);
            node.setSelected(operation);
            if (operation) {
                selectedNodesList.add(node);
            } else {
                selectedNodesList.remove(node);
            }
        }
    }

    /**
     * Metodo que retorna a entidade que deve ser ignorada na montagem do
     * treeview
     * 
     * @return
     */
    protected E getEntityToIgnore() {
        return null;
    }

    private UICollapsiblePanel getParentPanel(UIComponent root) {
        UIComponent parent = root.getParent();
        if (parent instanceof UICollapsiblePanel || parent == null) {
            return (UICollapsiblePanel) parent;
        }
        return getParentPanel(parent);
    }

    protected void closeParentPanel(UITree tree) {
        UICollapsiblePanel panel = getParentPanel(tree);
        if (panel != null) {
            panel.setExpanded(false);
        }
    }

    private GenericDAO genericDAO() {
        return ComponentUtil.getComponent(GenericDAO.NAME);
    }
    
}
