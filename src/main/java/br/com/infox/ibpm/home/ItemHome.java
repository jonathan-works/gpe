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
package br.com.infox.ibpm.home;

import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.annotations.manager.RecursiveManager;
import br.com.infox.ibpm.component.tree.ItemTreeHandler;
import br.com.infox.ibpm.entity.Item;
import br.com.infox.list.ItemList;
import br.com.itx.component.AbstractHome;


@Name(ItemHome.NAME)
@BypassInterceptors
public class ItemHome extends AbstractHome<Item> {
	private static final long serialVersionUID = 1L;
	private static final String TEMPLATE = "/Item/itemTemplate.xls";
	private static final String DOWNLOAD_XLS_NAME = "Items.xls";
	
	public static final String NAME = "itemHome";
	
	@Override
	public List<Item> getBeanList() {
		return ItemList.instance().list();
	}
	
	@Override
	public String getTemplate() {
		return TEMPLATE;
	}
	
	@Override
	public String getDownloadXlsName() {
		return DOWNLOAD_XLS_NAME;
	}

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
			return "updated";
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