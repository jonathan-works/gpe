package br.com.infox.seam.conversation;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Conversation;

@Name(ConversationFacade.NAME)
public class ConversationFacade {

    public static final String NAME = "conversationFacade";

    /**
     * Finaliza a conversação
     * 
     * @param toUrl
     * @return
     */

    // TODO Verificar como utilizar outcome em vez da url nos menus
    public String endBeforeRedirect(String toUrl) {
        Conversation.instance().root();
        Conversation.instance().endBeforeRedirect();
        return toUrl;
    }
}
