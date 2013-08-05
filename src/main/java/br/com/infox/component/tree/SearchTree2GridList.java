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

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.util.Strings;

import br.com.itx.component.grid.GridQuery;
import br.com.itx.exception.AplicationException;
import br.com.itx.util.ArrayUtil;
import br.com.itx.util.ComponentUtil;

public class SearchTree2GridList <E> {
	/**
	 * Nome do componente search da grid de pesquisa .
	 */
	private E searchBean;
	/**
	 * Nome do componente search da TreeView da pesquisa .
	 */
	private AbstractTreeHandler<E> treeHandler;
	/**
	 * Lista dos atributos referente aos campos de pesquisa(searchBean), ou seja,
	 * para que todos os campos fora a tree filtrem dados na pesquisa, deve ser 
	 * passado os seus respectivos nomes do Entity, cada um em uma posi��o do vetor.
	 */
	private String[] filterName;
	
	private GridQuery grid;
	
	/**
	 * Construtor padr�o.
	 * @param searchBean - Nome do searchBean da aba de pesquisa
	 * @param treeHandler - Nome do treeHandler criado para a treeView da pesquisa
	 */
	public SearchTree2GridList(E searchBean, AbstractTreeHandler<E> treeHandler) {
		this.searchBean = searchBean;
		this.treeHandler = treeHandler;
	}

	public void refreshTreeList() {
		treeHandler.clearTree();
	}
	
	/**
	 * M�todo que recebe os parametros e ativa os m�todos para constru��o da list.
	 * @return A lista que ser� exibida na Grid
	 */
	public List<EntityNode<E>> getList() {
		return getSearchTreeList();
	}
	
	/**
	 * M�todo que ir� montar a lista validando os devidos filtros.
	 * @return A lista que ser� exibida na Grid
	 */
	private List<EntityNode<E>> getSearchTreeList() {
		List<EntityNode<E>> result = new ArrayList<EntityNode<E>>();
		if(treeHandler.getSelected() != null) {
			for (EntityNode<E> node : treeHandler.getRoots()) {
				if (node.getEntity().equals(treeHandler.getSelected())) {
					result.add(node);
					getChildren(node, result);
				} else if (isChildren(node, false)) {
					EntityNode<E> selectedNode = getSelectedNode(node);
					result.add(selectedNode);
					getChildren(selectedNode, result);
				}
			}
		} else {
			for (EntityNode<E> node : treeHandler.getRoots()) {
				if (canAdd(node, result)) {
					result.add(node);
				}
				getChildren(node, result);
			}
		}
		return result;
	}

	/**
	 * Verifica se o registro selecionado � um dos filhos do n� em execu��o.
	 */
	private boolean isChildren(EntityNode<E> node, boolean ret){
		List<EntityNode<E>> childList = getChildList(node);
		for (EntityNode<E> e : childList) {
			if(e.equals(treeHandler.getSelected())) {
				return true;
			}
			ret = isChildren(e, ret);
		}
		return ret;
	}

	/**
	 * Retorna o n� selecionado que pertence a �rvore do registro pai informado. 
	 */
	private EntityNode<E> getSelectedNode(EntityNode<E> node) {
		List<EntityNode<E>> childList = getChildList(node);
		EntityNode<E> ret = null;
		for (EntityNode<E> e : childList) {
			if(e.getEntity().equals(treeHandler.getSelected())) {
				return e; 
			}
			ret = getSelectedNode(e);
		}
		return ret;
	}
	
	/**
	 * Verifica se todos os filhos, netos, bisnetos e etc.. devem ser adicionados
	 * na lista a ser exibida, atrav�s da recursividade.
	 * @param node - Representa o n� que ser�o verificados os registros da sua 
	 * sub �rvore.
	 * @param result A lista que ser� exibida na Grid
	 */
	private void getChildren(EntityNode<E> node, List<EntityNode<E>> result){
		List<EntityNode<E>> childList = getChildList(node);
		for (EntityNode<E> loc : childList) {
			if (canAdd(loc, result)) {
				result.add(loc);
			}
			getChildren(loc, result);
		}
	}

	/**
	 * M�todo de verifica��o chamado para cada n� nos m�todos getSearchTreeList()
	 * e getChildren() para informar se o registro deve ser adicionado ao 
	 * resultado da pesquisa. 
	 * @param node - N� que ser� validado
	 * @param result - A lista que ser� exibida na Grid
	 * @return Se True deve ser adicionado, se False, n�o deve.
	 */
	private boolean canAdd(EntityNode<E> node, List<EntityNode<E>> result) {
		boolean ret = isLogicOperatorAnd();
		if (searchBean != null) {
			if (filterName !=  null) {
				for (String atributeName : filterName) {
					Object searchField = ComponentUtil.getValue(searchBean, atributeName);
					Object nodeField = ComponentUtil.getValue(node.getEntity(), atributeName);
					if (searchField instanceof String) {
						//Caso o campo do search seja String e venha uam String vazia muda seu valor 
						//para null de modo que o filtro seja ignorado
						searchField = Strings.nullIfEmpty((String) searchField);
					}
					if (searchField != null) {
						if (nodeField instanceof String) {
							boolean condEval = nodeField.toString().toLowerCase()
									.contains(searchField.toString().toLowerCase());
							//Se a pesquisa na grid estiver usando qualquer 
							//express�o ele usa um 'or'
							if (isLogicOperatorAnd()) {
								ret &= condEval;
							} else {
								ret |= condEval;
							}
						} else {
							if (isLogicOperatorAnd()) {
								ret &= searchField.equals(nodeField);
							} else {
								ret |= searchField.equals(nodeField);
							}							
						}
						if (isLogicOperatorAnd()) {
							if(!ret) { 
							    return ret;
						    }
						} else {
							if(ret) {
							    return ret;
						    }									
						}						
					}
				}			
			}
		}
		return ret;
	}
		
	/**
	 * Faz a l�gica da identa��o para o filhos de um n�.
	 * @param e - N� a ser identado, ou n�o caso seja folha
	 * @param scape - Usado para identificar se ser� concatenado &#160 ou " "
	 * @return Retorna a string a ser exibida na Grid
	 */
	public String getIdent(EntityNode<E> e, boolean scape){
		StringBuilder ident = new StringBuilder();
		EntityNode<E> pai = e.getParent();
		while (pai != null) {
			if(scape) {
				ident.append("&#160;&#160;&#160;&#160;");
			} else {
				ident.append("    ");
			}
			if (pai == pai.getParent()) {
				throw new AplicationException("A tree esta em loop");
			}
			pai = pai.getParent();
		}
		return ident.toString();
	}
	
	/**
	 * Verifica se o registro � um n� folha, ou seja, se ele n�o possui pai.
	 * @param node - N� a ser verificado se � ou n�o pai
	 * @return True se for um registro folha
	 */
	public boolean isDad(EntityNode<E> node){
		return node.getParent() == null;
	}
	
	/**
	 * Obtem a lista dos registros filhos ao registro informado
	 * @param node - N� que ser� obtido a lista dos filhos
	 * @return A lista dos filhos de um determinado n�
	 */
	private List<EntityNode<E>> getChildList(EntityNode<E> node) {
		return node.getNodes();
	}
	
	/**
	 * Deve ser informado nesta vari�vel todos os nomes dos campos que dever�o
	 * realizar um filtro al�m da treeView.
	 * @param filterName - Vetor com os nomes dos atributos que far�o o filtro
	 */
	public void setFilterName(String[] filterName) {
		this.filterName = ArrayUtil.copyOf(filterName);
	}

	public void setGrid(GridQuery grid) {
		this.grid = grid;
	}

	public GridQuery getGrid() {
		return grid;
	}
	
	private boolean isLogicOperatorAnd() {
		return grid == null ? true : 
			"and".equalsIgnoreCase(grid.getRestrictionLogicOperator());
	}

}