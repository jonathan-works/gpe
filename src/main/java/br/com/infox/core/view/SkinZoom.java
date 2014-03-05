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
@BypassInterceptors
public class SkinZoom extends Selector {
    private static final long serialVersionUID = 1L;
    private String skinZoom = TAM_NORMAL;
    private static final String TAM_NORMAL = "12px";
    private static final String TAM_MEDIO = "18px";
    private static final String TAM_GRANDE = "22px";
    private static final String[] TAMANHOS = { TAM_NORMAL, TAM_MEDIO,
        TAM_GRANDE };

    public SkinZoom() {
        PathResolver pathResolver = (PathResolver) Component.getInstance(PathResolver.NAME);
        String cookiePath = pathResolver.getContextPath();
        setCookiePath(cookiePath);
        setCookieEnabled(true);
        String tamanhoCookie = getCookieValueIfEnabled();
        if (!Strings.isEmpty(tamanhoCookie)) {
            int i = Arrays.binarySearch(TAMANHOS, tamanhoCookie);
            if (i > -1) {
                skinZoom = tamanhoCookie;
            }
        }
    }

    public String getSkinZoom() {
        return skinZoom;
    }

    public void setTmNormal() {
        setCookieValueIfEnabled(TAM_NORMAL);
        skinZoom = TAM_NORMAL;
    }

    public void setTmMedio() {
        setCookieValueIfEnabled(TAM_MEDIO);
        skinZoom = TAM_MEDIO;
    }

    public void setTmGrande() {
        setCookieValueIfEnabled(TAM_GRANDE);
        skinZoom = TAM_GRANDE;
    }

    @Override
    protected String getCookieName() {
        return "br.com.infox.core.view";
    }
}
