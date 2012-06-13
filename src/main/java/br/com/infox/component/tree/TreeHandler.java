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

import java.util.List;

import org.richfaces.event.NodeSelectedEvent;

/**
 * Inteface que um componente deve implementar para manipular um treeview
 * @author luizruiz
 *
 */

public interface TreeHandler<E> {

	/**
	 * @return lista dos n�s do primeiro n�vel da �rvore
	 */
	List<EntityNode<E>> getRoots();

	
	/**
	 * Listener para atribuir o n� selecionado a um campo da classe, 
	 * usando o m�todo setSelected
	 * 
	 * @param ev objeto passado pelo treeview
	 */
	void selectListener(NodeSelectedEvent ev);

	
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
	 * Anula a sele��o da �rvore. A implementa��o deve chamar o m�todo 
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
	 * @param icon � o caminho para o icone de pastas
	 */
	void setIconFolder(String iconFolder);

	/**
	 * 
	 * @return caminho para o icone de folhas
	 */
	String getIconLeaf();

	/**
	 * 
	 * @param iconLeaf � o caminho para o icone de folhas
	 */
	void setIconLeaf(String iconLeaf);

	/**
	 * Indica se � permitido selecionar pastas
	 */
	boolean isFolderSelectable();

	/**
	 * 
	 * @param folderSelected determina se as pastas ser�o selecionaveis
	 */
	void setFolderSelectable(boolean folderSelectable);

}