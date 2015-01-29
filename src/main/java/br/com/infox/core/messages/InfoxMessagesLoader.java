package br.com.infox.core.messages;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;

@Stateless
public class InfoxMessagesLoader implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String EXTENDED_MESSAGES = "/extended_messages_%s.properties";
	private static final String ENTITY_MESSAGES = "/entity_messages_%s.properties";
	private static final String MESSAGES = "/messages_%s.properties";
	private static final String STANDARD_MESSAGES = "/standard_messages_%s.properties";
	private static final String PROCESS_DEFINITION_MESSAGES = "/process_definition_messages_%s.properties";
	private static final String VALIDATION_MESSAGES = "/ValidationMessages_%s.properties";
	
	@EJB
	private InfoxMessages infoxMessages;

	public void loadMessagesProperties() {
		Iterator<Locale> supportedLocales = getSupportedLocales();
		while (supportedLocales.hasNext()) {
			Locale locale = supportedLocales.next();

			String localeext = locale.toString();

			InputStream entityMessagesEppStream = getClass().getResourceAsStream(String.format(ENTITY_MESSAGES, localeext));
			InputStream messagesEppStream = getClass().getResourceAsStream(String.format(MESSAGES, localeext));
			InputStream processDefinitionStream = getClass()
					.getResourceAsStream(String.format(PROCESS_DEFINITION_MESSAGES, localeext));
			InputStream standardMessagesEppStream = getClass().getResourceAsStream(String.format(STANDARD_MESSAGES, localeext));
			InputStream validationMessagesStream = getClass().getResourceAsStream(String.format(VALIDATION_MESSAGES, localeext));
			InputStream extendedMessagesStream = getClass().getResourceAsStream(String.format(EXTENDED_MESSAGES, localeext));

			Map<String, String> mensagens = new HashMap<>();

			try {
				copyProperties(mensagens, entityMessagesEppStream, messagesEppStream, processDefinitionStream,
						standardMessagesEppStream, validationMessagesStream, extendedMessagesStream);
			} catch (IOException e) {
				// TODO Logar essa exceção
				e.printStackTrace();
			}
			infoxMessages.putInLocales(locale, mensagens);
		}

	}
	
	public void setInfoxMessages(InfoxMessages infoxMessages) {
		this.infoxMessages = infoxMessages;
	}

	private void copyProperties(Map<String, String> destination, InputStream... streamMessage) throws IOException {
		for (InputStream stream : streamMessage) {
			if (stream == null) continue;
			Properties source = new Properties();
			source.load(stream);
			Enumeration<Object> srcKeys = source.keys();
			while (srcKeys.hasMoreElements()) {
				String key = srcKeys.nextElement().toString();
				String value = source.getProperty(key);
				destination.put(key, value);
			}
		}
	}

	private Iterator<Locale> getSupportedLocales() {
		return FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
	}

}
