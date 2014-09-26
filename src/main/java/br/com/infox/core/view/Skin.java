package br.com.infox.core.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.model.SelectItem;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.Selector;
import org.jboss.seam.util.Strings;

import br.com.infox.seam.path.PathResolver;

@Name("wiSkin")
@Scope(ScopeType.SESSION)
@BypassInterceptors
public class Skin extends Selector {

    private static final long serialVersionUID = 1L;

    private String skin = "skin/default";

    private List<SelectItem> skins;

    private static final Map<String,String> SKIN_ENTRIES = getSkinEntries();
    
    private static final Map<String, String> getSkinEntries() {
        Map<String, String> simpleEntries = new HashMap<>();
        simpleEntries.put("skin/default", "Padrão");
        simpleEntries.put("skin/cinza", "Cinza");
        simpleEntries.put("skin/altoContraste", "AltoContraste");
        return simpleEntries;
    }

    public Skin() {
        PathResolver pathResolver = (PathResolver) Component.getInstance(PathResolver.NAME);
        String cookiePath = pathResolver.getContextPath();
        setCookiePath(cookiePath);
        setCookieEnabled(true);
        String skinCookie = getCookieValueIfEnabled();
        
        if (!Strings.isEmpty(skinCookie) && SKIN_ENTRIES.containsKey(skinCookie)) {
            skin = skinCookie;
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
        for (Entry<String, String> s : SKIN_ENTRIES.entrySet()) {
            skins.add(new SelectItem(s.getKey(),s.getValue()));
        }
        return skins;
    }

    @Override
    protected String getCookieName() {
        return "br.com.infox.ibpm.skin";
    }

}
