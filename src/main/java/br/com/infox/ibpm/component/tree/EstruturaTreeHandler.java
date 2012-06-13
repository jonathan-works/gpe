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
package br.com.infox.ibpm.component.tree;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.infox.ibpm.entity.Localizacao;


@Name(EstruturaTreeHandler.NAME)
@BypassInterceptors
public class EstruturaTreeHandler extends AbstractTreeHandler<Localizacao> {

	public static final String NAME = "estruturaTree";
	private static final long serialVersionUID = 1L;
	
	@Override
	protected String getQueryRoots() {
		return "select n from Localizacao n " 
		    + "where localizacaoPai is null and estrutura = true "
			+ "order by localizacao";
	}

	@Override
	protected String getQueryChildren() {
		return "select n from Localizacao n where localizacaoPai = :"
			+ EntityNode.PARENT_NODE;
	} 
	
	protected EntityNode<Localizacao> createNode() {
		return new EstruturaNode(getQueryChildrenList());
	}


}