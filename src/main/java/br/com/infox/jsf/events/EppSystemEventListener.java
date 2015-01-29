package br.com.infox.jsf.events;

import javax.faces.application.Application;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.com.infox.core.messages.InfoxMessagesLoader;
import br.com.infox.epp.processo.metadado.system.MetadadoLabelLoader;

public class EppSystemEventListener implements SystemEventListener {
	
	
	@Override
	public void processEvent(SystemEvent event) throws AbortProcessingException {
		try {
			InitialContext ic = new InitialContext();
			InfoxMessagesLoader infoxMessagesLoader = (InfoxMessagesLoader) ic.lookup("java:module/InfoxMessagesLoader");
			infoxMessagesLoader.loadMessagesProperties();
			MetadadoLabelLoader metadadoLabelLoader = (MetadadoLabelLoader) ic.lookup("java:module/MetadadoLabelLoader");
			metadadoLabelLoader.loadMetadadosMessagesProperties();
		} catch (NamingException | InstantiationException | IllegalAccessException e) {
			throw new AbortProcessingException(e);
		}
	}

	@Override
	public boolean isListenerForSource(Object source) {
		return source instanceof Application;
	}

}
