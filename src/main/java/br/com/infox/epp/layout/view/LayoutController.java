package br.com.infox.epp.layout.view;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import br.com.infox.core.server.ApplicationServerService;
import br.com.infox.epp.layout.manager.LayoutManager;
import br.com.infox.epp.layout.manager.SkinSessaoManager;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.util.ComponentUtil;

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

	public URL getResourceAsURL(String codigo) throws MalformedURLException {
	    String skin = skinSessaoManager.getSkin();
	    ApplicationServerService applicationServerService = ApplicationServerService.instance();
	    PathResolver pathResolver = ComponentUtil.getComponent(PathResolver.NAME);
	    return new URL(applicationServerService.getBaseResquestUrl() + pathResolver.getContextPath() + layoutManager.getResourceUrl(skin, codigo));
	}

	public String getResourceUrlByPath(String path) {
		String skin = skinSessaoManager.getSkin();
		return layoutManager.getResourceUrlByPath(skin, path);
	}
	
	public String getMaterialDesignIconJSFUrl(String dpir, String cor, String res, String nome){
            return String.format("/resources/styleSkinInfox/all/%s_web/ic_%s_%s_%s.png", dpir, nome, cor, res);
	}
	
	public String getMaterialDesignIconUrl(String dpir, String cor, String res, String nome){
	    String urlBase=FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
	    return String.format("%s/resources/styleSkinInfox/all/%s_web/ic_%s_%s_%s.png", urlBase, dpir, nome, cor, res); 
	}

	public String getMaterialDesignIconJsfUrl(String dpir, String cor, String res, String nome){
	    return String.format("/resources/styleSkinInfox/all/%s_web/ic_%s_%s_%s.png", dpir, nome, cor, res); 
	}
}
