package br.com.infox.epp.layout.view;

import static java.text.MessageFormat.format;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
	
	public String getResourcePath(String path) {
		String skin = skinSessaoManager.getSkin();
		return format("/rest/skin/{0}/{1}", skin, path);
		//return format("/resources/styleSkinInfox/{0}/{1}", skin, path);
	}


}
