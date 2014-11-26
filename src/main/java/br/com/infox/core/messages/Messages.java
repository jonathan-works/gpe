package br.com.infox.core.messages;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.contexts.Contexts;

import br.com.infox.epp.system.EppMessagesContextLoader;

public final class Messages {

    private static final Messages MESSAGES;
    static{
        MESSAGES = new Messages();
    }
    
    private Messages() {
    }
    
    public static final Messages getInstance(){
        return MESSAGES;
    }
    
    /**
     * Busca o valor de uma propriedade no arquivo de internacionalização
     * @param property a propriedade a ser avaliada
     * @return a tradução do propriedade
     * */
    public final String getMessage(String property) {
        String result = getMessages().get(property);
        if (result==null || result.trim().isEmpty()) {
            result = property;
        }
        return property;
    }
    
    public final static String resolveMessage(String property){
        return MESSAGES.getMessage(property);
    }
    
    @SuppressWarnings("unchecked")
    public final Map<String,String> getMessages(){
        Map<String,String> result;
        if (Contexts.isApplicationContextActive()){
            result = (Map<String, String>) Contexts.getApplicationContext().get(EppMessagesContextLoader.EPP_MESSAGES);
        } else {
            result = new HashMap<>();
        }
        return result;
    }

}
