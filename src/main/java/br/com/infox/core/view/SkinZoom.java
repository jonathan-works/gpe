package br.com.infox.core.view;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.Selector;

import br.com.infox.seam.path.PathResolver;

@Name("skinZoom")
@Scope(ScopeType.SESSION)
public class SkinZoom extends Selector {
    private static final long serialVersionUID = 1L;
    private int skinZoom = TAM_NORMAL;
    private static final Integer TAM_NORMAL = 16;
    private static final Integer TAM_MEDIO = 19;
    private static final Integer TAM_GRANDE = 22;
    public SkinZoom() {
        PathResolver pathResolver = (PathResolver) Component.getInstance(PathResolver.NAME);
        String cookiePath = pathResolver.getContextPath();
        setCookiePath(cookiePath);
        setCookieEnabled(true);
        String cookieValueIfEnabled = getCookieValueIfEnabled();
        Integer tamanhoCookie = cookieValueIfEnabled == null ? null : Integer.parseInt(cookieValueIfEnabled, 10);
        if (tamanhoCookie != null) {
            skinZoom = tamanhoCookie;
        }
    }

    public int getSkinZoom() {
        return skinZoom;
    }

    public void setTmNormal() {
        setCookieValueIfEnabled(TAM_NORMAL.toString());
        skinZoom = TAM_NORMAL;
    }

    public void setTmMedio() {
        setCookieValueIfEnabled(TAM_MEDIO.toString());
        skinZoom = TAM_MEDIO;
    }

    public void setTmGrande() {
        setCookieValueIfEnabled(TAM_GRANDE.toString());
        skinZoom = TAM_GRANDE;
    }

    public void normalizeZoom() {
        setCookieValueIfEnabled(TAM_NORMAL.toString());
        skinZoom = TAM_NORMAL;
    }

    public void increaseZoom() {
        setCookieValueIfEnabled(String.valueOf(++skinZoom));
    }

    public void decreaseZoom() {
        setCookieValueIfEnabled(String.valueOf(--skinZoom));
    }

    @Override
    protected String getCookieName() {
        return "br.com.infox.core.view";
    }
}
