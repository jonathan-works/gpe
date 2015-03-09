package br.com.infox.core.messages.remote;

import java.util.Locale;

import javax.ejb.Remote;

@Remote
public interface RemoteMessages {
	
	public String get(Object key, Locale locale);

}
