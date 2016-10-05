package br.com.infox.core.messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;

@Stateless
public class InfoxMessagesLoader {

    private static final String[] RESOURCE_MESSAGES = {"entity_messages", "messages", "standard_messages", "process_definition_messages", "ValidationMessages", "extended_messages"}; 
    
	@EJB
	private InfoxMessages infoxMessages;

	public void loadMessagesProperties() throws IOException {
	    List<String> resourceMessages = new ArrayList<>(Arrays.asList(RESOURCE_MESSAGES));
	    appendCustomizesMessages(resourceMessages);
		Iterator<Locale> supportedLocales = getSupportedLocales();
		
		while (supportedLocales.hasNext()) {
			Locale locale = supportedLocales.next();
			List<ResourceBundle> resourceBundles = new ArrayList<>(resourceMessages.size());
			for (String resourceMessage : resourceMessages) {
			    ResourceBundle resourceBundle = getResourceBundle(resourceMessage, locale);
			    if (resourceBundle != null) {
			        resourceBundles.add(resourceBundle);
			    }
			}

			Map<String, String> mensagens = generateMessages(resourceBundles);
			
			infoxMessages.putInLocales(locale, mensagens);
		}
	}
	
    protected void appendCustomizesMessages(List<String> resourceMessages) {
        // for customized messages
    }

    public void setInfoxMessages(InfoxMessages infoxMessages) {
		this.infoxMessages = infoxMessages;
	}
    
    private ResourceBundle getResourceBundle(String resourceName, Locale locale) {
        ResourceBundle resourceBundle = null;
        try {
            resourceBundle = ResourceBundle.getBundle(resourceName, locale);
        } catch (MissingResourceException e) {
            // do nothing
        }
        return resourceBundle;
    }

	private Map<String, String> generateMessages(List<ResourceBundle> resourceBundles) throws IOException {
	    Map<String, String> messages = new HashMap<>();
        for (ResourceBundle bundle : resourceBundles) {
            Enumeration<String> keys = bundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = bundle.getString(key);
                messages.put(key, value);
            }
        }
        return messages;
    }
	
	private Iterator<Locale> getSupportedLocales() {
		return FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
	}

}
