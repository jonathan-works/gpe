package br.com.infox.seam.messages;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.international.Messages;

public class LocaleUtil {

    private LocaleUtil() {
    }

    /**
     * Busca o valor de uma propriedade no arquivo de internacionalização
     * @param property a propriedade a ser avaliada
     * @return a tradução do propriedade
     * */
    public static String internacionalize(String property) {
        if (Contexts.isSessionContextActive()) {
            return Messages.instance().get(property);
        }
        return property;
    }

}
