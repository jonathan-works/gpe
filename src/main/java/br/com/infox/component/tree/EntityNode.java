

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

import javax.persistence.Query;

import br.com.itx.util.EntityUtil;


@SuppressWarnings("unchecked")
public class EntityNode<E> implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Indica o nome do parametro que cont�m o nivel pai dos n�s a serem
	 * retornados
	 */
	public static final String PARENT_NODE = "parent";
	protected E entity;
	protected E ignore;
	private boolean leaf;
	
	protected String[] queryChildren;
	
	protected List<EntityNode<E>> rootNodes;
	private List<EntityNode<E>> nodes;
	//Variavel para adi��o da selectBooleanCheckBox 
	private Boolean selected = false;
	protected EntityNode<E> parent;
 
	/**
	 * 
	 * @param queryChildren
	 *            query que retorna os n�s filhos da entidade selecionada
	 */
	public EntityNode(String queryChildren) {
		this.queryChildren = new String[1];
		this.queryChildren[0] = queryChildren;
	}

	public EntityNode(String[] queryChildren) {
		this.queryChildren = queryChildren;
	}

	public EntityNode(EntityNode<E> parent, E entity, String[] queryChildren) {
		this.queryChildren = queryChildren;
		this.parent = parent;
		this.entity = entity;
	}

	/**
	 * Busca os n�s filhos. Dispara um evento entityNodesPostGetNodes
	 * 
	 * @return lista de n�s filhos da entidade passada no construtor
	 */
	public List<EntityNode<E>> getNodes() {
		if (nodes == null) {
			nodes = new ArrayList<EntityNode<E>>();
			boolean parent = true;
			for (String query : queryChildren) {
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
		return nodes;
	}

	protected List<E> getChildrenList(String hql, E entity) {
		Query query = EntityUtil.createQuery(hql);
		return query.setParameter(PARENT_NODE, entity).getResultList();
	}

	protected EntityNode<E> createChildNode(E n) {
		return new EntityNode<E>(this, n, this.queryChildren);
	}

	protected EntityNode<E> createRootNode(E n) {
		return new EntityNode<E>(null, n, this.queryChildren);
	}
	
	/**
	 * 
	 * @return a entidade representada pelo n�
	 */
	public E getEntity() {
		return entity;
	}

	/**
	 * 
	 * @param queryRoots
	 *            query que retorna os n�s do primeiro n�vel
	 * @return lista dos n�s do primeiro n�vel
	 */
	public List<EntityNode<E>> getRoots(Query queryRoots) {
		if (rootNodes == null) {
			rootNodes = new ArrayList<EntityNode<E>>();
			List<E> roots = queryRoots.getResultList();
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
	 * Metodo que adiciona a entidade que deve ser ignorada na
	 * composi��o da tree
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

	public boolean canSelect() {
		return true;
	}
	
	public String[] getQueryChildren() {
		return queryChildren;
	}
}
