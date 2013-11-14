

/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.infox.core.tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import br.com.infox.util.constants.WarningConstants;
import br.com.itx.util.ArrayUtil;
import br.com.itx.util.EntityUtil;


public class EntityNode<E> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String PARENT_NODE = "parent";
	private E entity;
	private E ignore;
	private boolean leaf;
	
	private String[] queryChildrenList;
	
	private List<EntityNode<E>> rootNodes;
	private List<EntityNode<E>> nodes;
	//Variavel para adição da selectBooleanCheckBox 
	private Boolean selected = false;
	private EntityNode<E> parent;
 
	/**
	 * @param queryChildren
	 *            query que retorna os nós filhos da entidade selecionada
	 */
	public EntityNode(String queryChildren) {
		this.queryChildrenList = new String[] {queryChildren};
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
		return nodes;
	}
	
	protected String[] getQueryChildrenList() {
		return ArrayUtil.copyOf(queryChildrenList);
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	protected List<E> getChildrenList(String hql, E entity) {
		Query query = EntityUtil.createQuery(hql);
		return (List<E>) query.setParameter(PARENT_NODE, entity).getResultList();
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
	 * @param queryRoots
	 *            query que retorna os nós do primeiro nível
	 * @return lista dos nós do primeiro nível
	 */
	public List<EntityNode<E>> getRoots(Query queryRoots) {
		if (rootNodes == null) {
			rootNodes = new ArrayList<EntityNode<E>>();
			@SuppressWarnings(WarningConstants.UNCHECKED)
			List<E> roots = (List<E>) queryRoots.getResultList();
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
	 * composição da tree
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
		return ArrayUtil.copyOf(queryChildrenList);
	}
}
