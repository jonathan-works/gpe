package br.com.infox.core.view;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.Selector;
import org.jboss.seam.util.Strings;

import br.com.itx.component.Util;

@Name("wiSkin")
@Scope(ScopeType.SESSION)
@BypassInterceptors
public class Skin extends Selector {

    private static final long serialVersionUID = 1L;

    private String skin = "skin/azulVerde";

    private List<SelectItem> skins;

    private static final String[] SKINS = { "azulVerde", "cinza",
        "altoContraste" };

    public Skin() {
        Util util = (Util) Component.getInstance("util");
        String cookiePath = util.getContextPath();
        setCookiePath(cookiePath);
        setCookieEnabled(true);
        String skinCookie = getCookieValueIfEnabled();
        if (!Strings.isEmpty(skinCookie) && skinCookie.startsWith("skin/")) {
            for (String arg : SKINS) {
                if (arg.equalsIgnoreCase(skinCookie.substring(5))) {
                    skin = skinCookie;
                    break;
                }
            }
        }
    }

    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        setCookieValueIfEnabled(skin);
        this.skin = skin;
    }

    public List<SelectItem> getSkinList() {
        if (skins != null) {
            return skins;
        }
        skins = new ArrayList<SelectItem>();
        for (String s : SKINS) {
            skins.add(new SelectItem("skin/" + s, s));
        }
        return skins;
    }

    @Override
    protected String getCookieName() {
        return "br.com.infox.ibpm.skin";
    }

}
