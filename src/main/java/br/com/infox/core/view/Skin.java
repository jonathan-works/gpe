package br.com.infox.core.view;

import static java.text.MessageFormat.format;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.Selector;

import br.com.infox.epp.layout.dao.SkinDAO;
import br.com.infox.epp.layout.manager.SkinSessaoManager;
import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.util.ComponentUtil;

@Name("wiSkin")
@Scope(ScopeType.SESSION)
@BypassInterceptors
@Stateful
@SessionScoped
public class Skin extends Selector implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final String NOME_COOKIE_SKIN = "br.com.infox.ibpm.skin";

	private List<SelectItem> skins;

	private static Map<String, String> SKIN_ENTRIES;

	@Inject
	private SkinSessaoManager skinSessao;
	
	@Inject
	private SkinDAO skinDAO;

	public static Map<String, String> getSkinEntries() {
		return SKIN_ENTRIES;
	}

	public Skin() {
		PathResolver pathResolver = ComponentUtil.getComponent(PathResolver.NAME);
		String cookiePath = pathResolver.getContextPath();
		setCookiePath(cookiePath);
		setCookieEnabled(true);
	}

	@PostConstruct
	public void init() {
		if (SKIN_ENTRIES == null) {
			SKIN_ENTRIES = new HashMap<>();
			List<br.com.infox.epp.layout.entity.Skin> skins = skinDAO.findAll();
			for (br.com.infox.epp.layout.entity.Skin skin : skins) {
				SKIN_ENTRIES.put(skin.getCodigo(), skin.getNome());
			}
		}
	}

	@Remove
	public void destroy() {

	}

	// #{wiSkin.imageFolder}
	public String getImageFolder() {
		// "/resources/styleSkinInfox/default/imagens"
		return format("/resources/styleSkinInfox/{0}/imagens", getSkin());
	}

	public void setImageFolder(String folder) {
	}

	public String getSkin() {
		return skinSessao.getSkin();
	}

	public void setSkin(String skin) {
		skinSessao.setSkin(skin);
		skinSessao.setSkinCookie(skin);
	}

	public List<SelectItem> getSkinList() {
		if (skins != null) {
			return skins;
		}
		skins = new ArrayList<>();
		for (Entry<String, String> s : SKIN_ENTRIES.entrySet()) {
			skins.add(new SelectItem(s.getKey(), s.getValue()));
		}
		return skins;
	}

	@Override
	protected String getCookieName() {
		return NOME_COOKIE_SKIN;
	}

}
