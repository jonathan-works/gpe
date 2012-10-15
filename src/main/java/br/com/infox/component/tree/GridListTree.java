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

import org.jboss.seam.Component;

import br.com.itx.util.ComponentUtil;
import br.com.itx.util.EntityUtil;


public class GridListTree <E> {

	private String query;
	
	private String nameAtributeChildList;
	private String nameAtributeFather;

	private String searchName;

	
	private E getFather(E obj) {
		return (E) ComponentUtil.getValue(obj, nameAtributeFather);
	}
	
	private E getAtivo(E obj) {
		return (E) ComponentUtil.getValue(obj, "ativo");
	}	
	
	private List<E> getChildList(E obj) {
		return (List<E>) ComponentUtil.getValue(obj, nameAtributeChildList);
	}
	
	public List<E> getLocalizacaoList() {
		List<E> localizacaoList = new ArrayList<E>();
		List<E> list = EntityUtil.getEntityManager().createQuery(query)
			.getResultList();
		for (E l : list) {
			if (canAdd(l)) {
				localizacaoList.add(l);
			}
			getChildren(l, localizacaoList);
		}
		return localizacaoList;
	}
	
	private void getChildren(E node, List<E> resultado){
		List<E> localizacaoList = getChildList(node);
		for (E loc : localizacaoList) {
			if (canAdd(loc)) {
				resultado.add(loc);
			}
			getChildren(loc, resultado);
		}
	}
	
	private boolean canAdd(E node) {
		boolean ret = true;
		E search = (E) Component.getInstance(searchName);
		
		if (search != null) {
			if (search.toString() != null) {
				ret &= node.toString().toLowerCase().contains(search.toString().toLowerCase());
			}
			Boolean ativoSearch = (Boolean) getAtivo(search);
			Boolean ativoNode = (Boolean) getAtivo(node);
			if (ativoSearch != null) {
				ret &= ativoNode.equals(ativoSearch);
			}
		}

		AbstractTreeHandler<E> searchTree = (AbstractTreeHandler<E>) Component.getInstance("localizacaoSearchTree");
		if (searchTree != null && searchTree.getSelected() != null) {
			E father = getFather(node);
			if (father == null)  {
				ret &= node.equals(searchTree.getSelected());
			} else {
				ret &= father.equals(searchTree.getSelected());
			}
		}
		return ret;
	}
	
	public String treeOrdenada (E node) {
		String nome;
		E pai = node;
		nome = pai.toString();
		pai = getFather(pai);
		if (getFather(node) == null) {
			nome = "<b>" + nome + "</b>";
		}
		while (pai != null) {
			if (nome.length() > 0) {
				nome = "&#160;&#160;&#160;" + nome;
			}	
			if (pai.equals(getFather(pai))) {
				throw new RuntimeException("O nivel de TUA esta em loop");
			}
			pai = getFather(pai);
		}
		return nome;
	}
		
	
}