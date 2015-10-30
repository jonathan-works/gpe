package br.com.infox.core.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

@AutoCreate
@Name(InfoxMessages.NAME)
@Singleton(name = InfoxMessages.NAME)
@Lock(LockType.READ)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class InfoxMessages extends HashMap<String, String> implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "infoxMessages";
	
	private Map<Locale, Map<String, String>> locales = new HashMap<>();
	
	private Locale getDefaultLocale() {
		return FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
	}
	
	private Locale getRequestLocale() {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null){
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
		if (locales.containsKey(requestLocale)) {
			return requestLocale;
		}
		String base = requestLocale.toString().split("_")[0]; // TODO Mudar para stripExtensions quando puder usar Java 8
		Locale baseLocale = new Locale(base);
		if (locales.containsKey(baseLocale)) {
			return baseLocale;
		}
		return getDefaultLocale();
	}
	
	public String get(Object key) {
		Map<String, String> map = locales.get(getRequestLocale());
		return (String) (map != null && map.containsKey(key) ? map.get(key) : key);
	}
	
	public String get(Object key, Locale locale) {
		Map<String, String> map = locales.get(locale);
		if (map == null) {
			throw new IllegalStateException("Não existe locale" + locale);
		}
		return (String) (map.containsKey(key) ? map.get(key) : key);
	}
	
	private Map<String, String> getMessages() {
		return locales.get(getRequestLocale());
	}
	
	public boolean containsKey(String key) {
		return getMessages().containsKey(key);
	}
	
	public void putInLocales(Locale locale, Map<String, String> map) {
		locales.put(locale, map);
	}
	
	public static InfoxMessages getInstance() {
		try {
			InitialContext ic = new InitialContext();
			return (InfoxMessages) ic.lookup("java:module/infoxMessages");
		} catch (NamingException e) {
			return new InfoxMessages();
		}
	}
}
