package br.com.infox.core.view;

import static java.text.MessageFormat.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.Selector;
import org.jboss.seam.util.Strings;

import br.com.infox.seam.path.PathResolver;
import br.com.infox.seam.util.ComponentUtil;

@Name("wiSkin")
@Scope(ScopeType.SESSION)
@BypassInterceptors
public class Skin extends Selector {

    private static final long serialVersionUID = 1L;

    private String skin = "default";

    private List<SelectItem> skins;

    private static final Map<String,String> SKIN_ENTRIES = getSkinEntries();
    
    private static final Map<String, String> getSkinEntries() {
        Map<String, String> simpleEntries = new HashMap<>();
        simpleEntries.put("default", "Padrão");
        simpleEntries.put("cinza", "Cinza");
        simpleEntries.put("altoContraste", "AltoContraste");
        return simpleEntries;
    }

    public Skin() {
        PathResolver pathResolver = ComponentUtil.getComponent(PathResolver.NAME);
        String cookiePath = pathResolver.getContextPath();
        setCookiePath(cookiePath);
        setCookieEnabled(true);
        String skinCookie = getCookieValueIfEnabled();
        
        if (!Strings.isEmpty(skinCookie) && SKIN_ENTRIES.containsKey(skinCookie)) {
            skin = skinCookie;
        }
    }
// #{wiSkin.imageFolder}
    public String getImageFolder(){
        //"/resources/styleSkinInfox/default/imagens"
        return format("/resources/styleSkinInfox/{0}/imagens", getSkin());
    }
    public void setImageFolder(String folder){
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
        skins = new ArrayList<>();
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
