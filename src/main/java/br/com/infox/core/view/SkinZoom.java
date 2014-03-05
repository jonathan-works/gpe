package br.com.infox.core.view;

import java.util.Arrays;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.Selector;
import org.jboss.seam.util.Strings;

import br.com.infox.seam.path.PathResolver;

@Name("skinZoom")
@Scope(ScopeType.SESSION)
public class SkinZoom extends Selector {
    private static final long serialVersionUID = 1L;
    private int skinZoom = TAM_NORMAL;
    private static final Integer TAM_NORMAL = 12;
    private static final Integer TAM_MEDIO = 17;
    private static final Integer TAM_GRANDE = 22;
    private static final Integer[] TAMANHOS = { TAM_NORMAL, TAM_MEDIO,
        TAM_GRANDE };

    public SkinZoom() {
        PathResolver pathResolver = (PathResolver) Component.getInstance(PathResolver.NAME);
        String cookiePath = pathResolver.getContextPath();
        setCookiePath(cookiePath);
        setCookieEnabled(true);
        String cookieValueIfEnabled = getCookieValueIfEnabled();
        Integer tamanhoCookie = cookieValueIfEnabled == null ? null : Integer.parseInt(cookieValueIfEnabled, 10);
        if (tamanhoCookie != null) {
            int i = Arrays.binarySearch(TAMANHOS, tamanhoCookie);
            if (i > -1) {
                skinZoom = tamanhoCookie;
            }
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

    @Override
    protected String getCookieName() {
        return "br.com.infox.core.view";
    }
}
