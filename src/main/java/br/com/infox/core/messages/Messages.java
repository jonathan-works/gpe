package br.com.infox.core.messages;

import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.contexts.Contexts;

import br.com.infox.epp.system.EppMessagesContextLoader;

public final class Messages {

    private static final class LazyHolder {
        private static final Messages INSTANCE = new Messages();
    }

    private Messages() {
    }

    public static final Messages getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * Busca o valor de uma propriedade no arquivo de internacionalização
     *
     * @param property
     *            a propriedade a ser avaliada
     * @return a tradução do propriedade
     * */
    public final String getMessage(final String property) {
        String result = getMessages().get(property);
        if ((result == null) || result.trim().isEmpty()) {
            result = property;
        }
        return result;
    }

    /**
     * Busca o valor de uma propriedade no arquivo de internacionalização de
     * forma estática
     *
     * @param property
     *            a propriedade a ser avaliada
     * @return a tradução da propriedade
     */
    public final static String resolveMessage(final String property) {
        return LazyHolder.INSTANCE.getMessage(property);
    }

    @SuppressWarnings("unchecked")
    public final Map<String, String> getMessages() {
        Map<String, String> result;
        if (Contexts.isApplicationContextActive()) {
            result = (Map<String, String>) Contexts.getApplicationContext().get(EppMessagesContextLoader.EPP_MESSAGES);
        } else {
            result = new HashMap<>();
        }
        return result;
    }

}
