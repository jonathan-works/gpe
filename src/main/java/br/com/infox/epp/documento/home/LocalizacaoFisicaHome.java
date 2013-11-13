package br.com.infox.epp.documento.home;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import br.com.infox.annotations.manager.RecursiveManager;
import br.com.infox.epp.documento.component.tree.LocalizacaoFisicaTreeHandler;
import br.com.infox.epp.documento.entity.LocalizacaoFisica;
import br.com.itx.component.AbstractHome;

@Name(LocalizacaoFisicaHome.NAME)
@Scope(ScopeType.PAGE)
@Deprecated
public class LocalizacaoFisicaHome extends AbstractHome<LocalizacaoFisica>{

	public static final String NAME = "localizacaoFisicaHome";
	private static final long serialVersionUID = 1L;
	
	public void limparTrees() {
		LocalizacaoFisicaTreeHandler lfth = getComponent(LocalizacaoFisicaTreeHandler.NAME);
		lfth.clearTree();
	}
	
	@Override
	public void newInstance() {
		super.newInstance();
		limparTrees();
	}
	
	@Override
	public String inactive(LocalizacaoFisica localizacaoFisica) {
		RecursiveManager.inactiveRecursive(localizacaoFisica);
		return super.inactive(localizacaoFisica);
	}
	
	@Override
	protected boolean beforePersistOrUpdate() {
		if (instance.getLocalizacaoFisicaPai() != null && !instance.getLocalizacaoFisicaPai().getAtivo()){
		    instance.setAtivo(false);
		}
		return true;
	}
	

	@Override
	public String update() {
		if (!getInstance().getAtivo()){
			RecursiveManager.inactiveRecursive(getInstance());
		} 
		return super.update();
	}

}
