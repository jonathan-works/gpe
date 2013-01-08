package br.com.infox.ibpm.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import br.com.infox.annotations.manager.RecursiveManager;
import br.com.infox.core.action.list.EntityList;
import br.com.infox.ibpm.component.tree.LocalizacaoFisicaTreeHandler;
import br.com.infox.ibpm.entity.LocalizacaoFisica;
import br.com.infox.list.LocalizacaoFisicaList;
import br.com.itx.component.AbstractHome;

@Name(LocalizacaoFisicaHome.NAME)
@BypassInterceptors
@Scope(ScopeType.PAGE)
public class LocalizacaoFisicaHome extends AbstractHome<LocalizacaoFisica>{

	public static final String NAME = "localizacaoFisicaHome";
	private static final long serialVersionUID = 1L;
	
	@Override
	public EntityList<LocalizacaoFisica> getBeanList() {
		return LocalizacaoFisicaList.instance();
	}
	
	public void limparTrees() {
		LocalizacaoFisicaTreeHandler lfth = getComponent(LocalizacaoFisicaTreeHandler.NAME);
		lfth.clearTree();
	}
	
	@Override
	public void newInstance() {
		limparTrees();
		super.newInstance();
	}
	
	@Override
	public String inactive(LocalizacaoFisica localizacaoFisica) {
		RecursiveManager.inactiveRecursive(localizacaoFisica);
		return super.inactive(localizacaoFisica);
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
			((LocalizacaoFisicaTreeHandler)getComponent("localizacaoFisicaTree")).setSelected(getInstance().getLocalizacaoFisicaPai());
		}
	}
}
