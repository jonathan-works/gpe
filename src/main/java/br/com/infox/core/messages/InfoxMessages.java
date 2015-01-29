package br.com.infox.core.messages;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.ejb.Singleton;
import javax.faces.context.FacesContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

@AutoCreate
@Name(InfoxMessages.NAME)
@Singleton(name = InfoxMessages.NAME)
public class InfoxMessages extends HashMap<String, String> implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static final String NAME = "infoxMessages";
	
	private Map<Locale, Map<String, String>> locales = new HashMap<>();
	
	private Iterator<Locale> getSupportedLocales() {
		return FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
	}
	
	private Locale getDefaultLocale() {
		return FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
	}
	
	private Locale getRequestLocale() {
		Locale requestLocale = FacesContext.getCurrentInstance().getExternalContext().getRequestLocale();
		return !locales.containsKey(requestLocale) ? getDefaultLocale() : requestLocale;
	}
	
	public String get(Object key) {
		Map<String, String> map = locales.get(getRequestLocale());
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
