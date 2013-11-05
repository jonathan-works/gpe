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
package br.com.infox.epp.fluxo.tree;

import org.jboss.seam.annotations.Name;
import br.com.infox.component.tree.AbstractTreeHandler;
import br.com.infox.component.tree.EntityNode;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.itx.util.ComponentUtil;


@Name(ItemTreeHandler.NAME)
public class ItemTreeHandler extends AbstractTreeHandler<Item> {

	public static final String NAME = "itemTree";
	private static final long serialVersionUID = 1L;
	
	@Override
	protected String getQueryRoots() {
		return "select n from Item n "
			+ "where itemPai is null "
			+ "order by descricaoItem";
	}

	@Override
	protected String getQueryChildren() {
		return "select n from Item n where itemPai = :"
		+ EntityNode.PARENT_NODE;
	}

	@Override
	protected Item getEntityToIgnore() {
		return ComponentUtil.getInstance("itemHome");
	}
		
}

