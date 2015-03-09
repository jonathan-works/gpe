package br.com.infox.core.messages.remote;

import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.com.infox.core.messages.InfoxMessages;

@Stateless
public class InfoxRemoteMessages implements RemoteMessages {
	
	@EJB
	private InfoxMessages infoxMessages;

	@Override
	public String get(Object key, Locale locale) {
		return infoxMessages.get(key, locale);
	}

}
