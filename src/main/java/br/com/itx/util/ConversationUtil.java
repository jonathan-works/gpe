package br.com.itx.util;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.Conversation;

@Name(ConversationUtil.NAME)
public class ConversationUtil {

	public static final String NAME = "conversationUtil";

	/**
	 * Finaliza a conversação
	 * @param toUrl
	 * @return
	 */
	
	//TODO Verificar como utilizar outcome em vez da url nos menus
	public String endBeforeRedirect(String toUrl) {
		Conversation.instance().root();
		Conversation.instance().endBeforeRedirect();
		return toUrl;
	}
}