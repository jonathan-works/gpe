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
	 * passado os seus respectivos nomes do Entity, cada um em uma posição do vetor.
	 */
	private String[] filterName;
	
	private GridQuery<?> grid;
	
	/**
	 * Construtor padrão.
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
	 * Método que recebe os parametros e ativa os métodos para construção da list.
	 * @return A lista que será exibida na Grid
	 */
	public List<EntityNode<E>> getList() {
		return getSearchTreeList();
	}
	
	/**
	 * Método que irá montar a lista validando os devidos filtros.
	 * @return A lista que será exibida na Grid
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
				if (canAdd(node)) {
					result.add(node);
				}
				getChildren(node, result);
			}
		}
		return result;
	}

	/**
	 * Verifica se o registro selecionado é um dos filhos do nó em execução.
	 */
	private boolean isChildren(EntityNode<E> node, boolean ret){
	    boolean retorno = ret;
		List<EntityNode<E>> childList = getChildList(node);
		for (EntityNode<E> e : childList) {
			if(e.equals(treeHandler.getSelected())) {
				return true;
			}
			retorno = isChildren(e, ret);
		}
		return retorno;
	}

	/**
	 * Retorna o nó selecionado que pertence a árvore do registro pai informado. 
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
	 * na lista a ser exibida, através da recursividade.
	 * @param node - Representa o nó que serão verificados os registros da sua 
	 * sub árvore.
	 * @param result A lista que será exibida na Grid
	 */
	private void getChildren(EntityNode<E> node, List<EntityNode<E>> result){
		List<EntityNode<E>> childList = getChildList(node);
		for (EntityNode<E> loc : childList) {
			if (canAdd(loc)) {
				result.add(loc);
			}
			getChildren(loc, result);
		}
	}

	/**
	 * Método de verificação chamado para cada nó nos métodos getSearchTreeList()
	 * e getChildren() para informar se o registro deve ser adicionado ao 
	 * resultado da pesquisa. 
	 * @param node - Nó que será validado
	 * @param result - A lista que será exibida na Grid
	 * @return Se True deve ser adicionado, se False, não deve.
	 */
	private boolean canAdd(EntityNode<E> node) {
		boolean ret = isLogicOperatorAnd();
		if (searchBean != null && filterName !=  null) {
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
						//expressão ele usa um 'or'
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
		return ret;
	}
		
	/**
	 * Faz a lógica da identação para o filhos de um nó.
	 * @param e - Nó a ser identado, ou não caso seja folha
	 * @param scape - Usado para identificar se será concatenado &#160 ou " "
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
	 * Verifica se o registro é um nó folha, ou seja, se ele não possui pai.
	 * @param node - Nó a ser verificado se é ou não pai
	 * @return True se for um registro folha
	 */
	public boolean isDad(EntityNode<E> node){
		return node.getParent() == null;
	}
	
	/**
	 * Obtem a lista dos registros filhos ao registro informado
	 * @param node - Nó que será obtido a lista dos filhos
	 * @return A lista dos filhos de um determinado nó
	 */
	private List<EntityNode<E>> getChildList(EntityNode<E> node) {
		return node.getNodes();
	}
	
	/**
	 * Deve ser informado nesta variável todos os nomes dos campos que deverão
	 * realizar um filtro além da treeView.
	 * @param filterName - Vetor com os nomes dos atributos que farão o filtro
	 */
	public void setFilterName(String[] filterName) {
		this.filterName = ArrayUtil.copyOf(filterName);
	}

	public void setGrid(GridQuery<?> grid) {
		this.grid = grid;
	}

	public GridQuery<?> getGrid() {
		return grid;
	}
	
	private boolean isLogicOperatorAnd() {
		return grid == null ? true : 
			"and".equalsIgnoreCase(grid.getRestrictionLogicOperator());
	}

}