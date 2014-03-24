package br.com.infox.seam.messages;

import org.jboss.seam.international.Messages;

public class MessagesUtil {

    private MessagesUtil() {
    }

    /**
     * Busca o valor de uma propriedade no arquivo de internacionalização
     * @param property a propriedade a ser avaliada
     * @return a tradução do propriedade
     * */
    public static String internacionalize(String property) {
        return Messages.instance().get(property);
    }

}
