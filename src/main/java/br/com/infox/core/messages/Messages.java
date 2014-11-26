package br.com.infox.core.messages;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.contexts.Contexts;

import br.com.infox.epp.system.EppMessagesContextLoader;

public final class Messages {

    private static final Messages messages;
    static{
        messages = new Messages();
    }
    
    private Messages() {
    }
    
    public static final Messages getInstance(){
        return messages;
    }

    /**
     * Busca o valor de uma propriedade no arquivo de internacionalização
     * @param property a propriedade a ser avaliada
     * @return a tradução do propriedade
     * */
    public String getMessage(String property) {
        String result = getMessageMap().get(property);
        if (result==null || result.trim().isEmpty()) {
            result = property;
        }
        return property;
    }
    
    @SuppressWarnings("unchecked")
    private Map<String,String> getMessageMap(){
        Map<String,String> result;
        if (Contexts.isApplicationContextActive()){
            result = (Map<String, String>) Contexts.getApplicationContext().get(EppMessagesContextLoader.EPP_MESSAGES);
        } else {
            result = new HashMap<>();
        }
        return result;
    }

}
