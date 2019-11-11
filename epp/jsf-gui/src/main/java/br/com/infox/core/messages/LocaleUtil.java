package br.com.infox.core.messages;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named
@RequestScoped
public class LocaleUtil implements Serializable {

    private static final long serialVersionUID = 1L;

    private Locale getDefaultLocale() {
        return FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
    }

    private Iterator<Locale> getSupportedLocales() {
        return FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
    }

    public Locale getRequestLocale() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        if (facesContext == null) {
            return Locale.getDefault();
        }
        ExternalContext externalContext = facesContext.getExternalContext();
        if (externalContext == null) {
            return Locale.getDefault();
        }
        Locale requestLocale = externalContext.getRequestLocale();
        if (requestLocale == null) {
            return getDefaultLocale();
        }
        Iterator<Locale> supportedLocales = getSupportedLocales();
        for (Locale loc; supportedLocales.hasNext();) {
            loc = supportedLocales.next();
            if (Objects.equals(loc, requestLocale)) {
                return requestLocale;
            } else {
                String base = requestLocale.toString().split("_")[0]; // TODO Mudar para stripExtensions quando puder usar Java 8
                Locale baseLocale = new Locale(base);
                if (Objects.equals(loc, baseLocale)) {

                }
            }
        }

        return getDefaultLocale();
    }

}
