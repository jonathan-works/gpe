package br.com.infox.epp.layout.view;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
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
	
	
	public String getResourceUrl(String codigo) {
		String skin = skinSessaoManager.getSkin();
		return layoutManager.getResourceUrl(skin, codigo);
	}

	public String getResourceUrlByPath(String path) {
		String skin = skinSessaoManager.getSkin();
		return layoutManager.getResourceUrlByPath(skin, path);
	}
	
	public String getMaterialDesignIconUrl(String dpir, String cor, String res, String nome){
	    String urlBase=FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
	    return String.format("%s/resources/styleSkinInfox/all/%s_web/ic_%s_%s_%s.png", urlBase, dpir, nome, cor, res); 
	}

}
