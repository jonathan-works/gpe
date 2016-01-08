package br.com.infox.epp.layout.view;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.epp.layout.manager.LayoutManager;
import br.com.infox.epp.layout.manager.SkinSessaoManager;

@Named
@SessionScoped
public class LayoutController implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Inject
	private SkinSessaoManager skinSessaoManager;
	
	@Inject
	private LayoutManager layoutManager;
	
	public String getResourcePath(String path) {
		String skin = skinSessaoManager.getSkin();
		return layoutManager.getResourcePath(skin, path);
	}


}
