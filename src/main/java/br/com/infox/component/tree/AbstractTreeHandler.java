/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
 */
package br.com.infox.component.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.util.StopWatch;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;
import org.richfaces.function.RichFunction;

import br.com.itx.util.EntityUtil;

@Scope(ScopeType.CONVERSATION)
public abstract class AbstractTreeHandler<E> implements TreeHandler<E>,
		Serializable {

	private static final LogProvider LOG = Logging
			.getLogProvider(AbstractTreeHandler.class);

	private static final long serialVersionUID = 1L;

	private E selected;

	protected List<EntityNode<E>> rootList;

	protected String treeId;

	private String iconFolder;

	private String iconLeaf;

	private boolean folderSelectable = true;

	private String expression;

	private List<EntityNode<E>> selectedNodesList = new ArrayList<EntityNode<E>>(
			0);

	@Override
	public void clearTree() {
		selectedNodesList = new ArrayList<EntityNode<E>>();
		rootList = null;
		selected = null;
		clearUITree();
		if (expression != null) {
			Expressions.instance().createValueExpression(expression)
					.setValue(null);
		}
	}

	private void clearUITree() {
		if (treeId != null) {
			javax.faces.component.UIComponent comp = RichFunction
					.findComponent(treeId);

			if (!comp.getClass().equals(UITree.class))
				return;

			UITree tree = (UITree) comp;
			tree.setRowKey(null);
			tree.setSelection(null);
		}
	}

	@Override
	public List<EntityNode<E>> getRoots() {
		if (rootList == null) {
			StopWatch sw = new StopWatch(true);
			Query queryRoots = getEntityManager().createQuery(getQueryRoots());
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
	 * Lista de queries que ir�o gerar os n�s filhos Caso haja mais de uma
	 * query, deve-se sobrescrever esse m�todo e retornar null no m�todo
	 * getQueryChildren()
	 * 
	 * @return
	 */
	protected String[] getQueryChildrenList() {
		String[] children = new String[1];
		children[0] = getQueryChildren();
		return children;
	}

	protected EntityManager getEntityManager() {
		return EntityUtil.getEntityManager();
	}

	@Override
	@SuppressWarnings("unchecked")
	public E getSelected() {
		if (expression == null) {
			return selected;
		}
		Object value = null;
		try {
			value = Expressions.instance().createValueExpression(expression)
					.getValue();
		} catch (Exception ignore) {
			ignore.printStackTrace();
		}
		return (E) value;
	}

	@Override
	public void setSelected(E selected) {
		if (expression == null) {
			this.selected = selected;
		} else {
			Expressions.instance().createValueExpression(expression)
					.setValue(selected);
		}
	}

	@Override
	public void processTreeSelectionChange(TreeSelectionChangeEvent ev) {
		// Considerando single selection
		Object selectionKey = new ArrayList<Object>(ev.getNewSelection()).get(0);
		UITree tree = (UITree) ev.getSource();
		treeId = tree.getId();

		Object key = tree.getRowKey();
		tree.setRowKey(selectionKey);
		EntityNode<E> en = (EntityNode<E>) tree.getRowData();
		tree.setRowKey(key);
		setSelected(en.getEntity());
		Events.instance().raiseEvent(getEventSelected(), getSelected());
	}

	protected String getEventSelected() {
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
	 * Tratamento para que a string n�o fique maior que o tamanho do campo
	 * 
	 * @param selected
	 * @return
	 */
	public String getSelectedView(E selected) {
		String selecionado = "";
		if (selected == null || selected.toString() == null) {
			return selecionado;
		}
		if (selected.toString().length() > 25) {
			selecionado = selected.toString().substring(0, 25) + "...";
		} else {
			selecionado = selected.toString();
		}
		return selecionado;
	}

	/**
	 * M�todo que retorna a lista dos itens selecionados.
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
	 * Insere o n� selecionado pela checkBox na lista dos n�s selecionados.
	 * 
	 * @param node
	 *            - N� selecionado pelo usu�rio
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

}