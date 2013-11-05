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
package br.com.infox.epp.fluxo.home;

import org.jboss.seam.annotations.Name;
import br.com.infox.annotations.manager.RecursiveManager;
import br.com.infox.epp.fluxo.entity.Item;
import br.com.infox.epp.fluxo.tree.ItemTreeHandler;
import br.com.itx.component.AbstractHome;

@Name(ItemHome.NAME)
public class ItemHome extends AbstractHome<Item> {
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "itemHome";
	
	public void limparTrees() {
		ItemTreeHandler ith = getComponent(ItemTreeHandler.NAME);
		ith.clearTree();
	}
	
	@Override
	public void newInstance() {
		limparTrees();
		super.newInstance();
	}
	
	@Override
	public String inactive(Item item) {
		RecursiveManager.inactiveRecursive(item);
		return super.inactive(item);
	}

	@Override
	public String update() {
		if (!getInstance().getAtivo()){
			RecursiveManager.inactiveRecursive(getInstance());
		} 
		return super.update();
	}

	@Override
	public void setId(Object id) {
		super.setId(id);
		if(isManaged()) {
			((ItemTreeHandler)getComponent("itemTree")).setSelected(getInstance().getItemPai());
		}
	}
}