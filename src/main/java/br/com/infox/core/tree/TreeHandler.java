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

import java.util.List;

import org.richfaces.event.TreeSelectionChangeListener;

/**
 * Inteface que um componente deve implementar para manipular um treeview
 * @author luizruiz
 *
 */

public interface TreeHandler<E> extends TreeSelectionChangeListener {

	/**
	 * @return lista dos nós do primeiro nível da árvore
	 */
	List<EntityNode<E>> getRoots();
	
	/**
	 * @return entidade selecionada no treeview
	 */
	E getSelected();

	
	/**
	 * Seta a entidade selecionada
	 * @param selected
	 */
	void setSelected(E selected);
	
	/**
	 * Anula a seleção da árvore. A implementação deve chamar o método 
	 * setSelected(null).
	 * 
	 */
	void clearTree();

	/**
	 * 
	 * @return caminho para o icone de pastas
	 */
	String getIconFolder();
	
	/**
	 * 
	 * @param icon é o caminho para o icone de pastas
	 */
	void setIconFolder(String iconFolder);

	/**
	 * 
	 * @return caminho para o icone de folhas
	 */
	String getIconLeaf();

	/**
	 * 
	 * @param iconLeaf é o caminho para o icone de folhas
	 */
	void setIconLeaf(String iconLeaf);

	/**
	 * Indica se é permitido selecionar pastas
	 */
	boolean isFolderSelectable();

	/**
	 * 
	 * @param folderSelected determina se as pastas serão selecionaveis
	 */
	void setFolderSelectable(boolean folderSelectable);

}