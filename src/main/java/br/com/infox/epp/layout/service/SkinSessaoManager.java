package br.com.infox.epp.layout.service;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.faces.Selector;

import br.com.infox.epp.layout.dao.SkinDAO;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.util.ComponentUtil;

@SessionScoped
public class SkinSessaoManager implements Serializable {
	public static final String NOME_COOKIE_SKIN = br.com.infox.core.view.Skin.NOME_COOKIE_SKIN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String skin;

	@Inject
	private SkinDAO skinDAO;
	
	public void setSkinCookie(String skin) {
		PathResolver pathResolver = ComponentUtil.getComponent(PathResolver.NAME);
		Cookie cookie = new Cookie(NOME_COOKIE_SKIN, skin);
		cookie.setMaxAge(Selector.DEFAULT_MAX_AGE);
		cookie.setPath(pathResolver.getContextPath());
		
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		response.addCookie(cookie);
	}
	
	public String getSkinCookie() {
		Cookie cookie = (Cookie)FacesContext.getCurrentInstance().getExternalContext().getRequestCookieMap().get(NOME_COOKIE_SKIN);
		if(cookie != null) {
			return cookie.getValue();
		}
		return null;
	}
	

	public synchronized String getSkin() {
		if(skin == null) {
			String skinCookie = getSkinCookie();
			if(skinCookie != null) {
				skin = skinCookie;
			}
			else
			{
				skin = skinDAO.getSkinPadrao().getCodigo();
			}
		}
		return skin;
	}

	public void setSkin(String skin) {
		this.skin = skin;
	}

}
