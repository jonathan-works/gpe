package br.com.itx.util;

import java.util.List;

import org.jboss.seam.util.Strings;

public final class StringUtil {
	
    private StringUtil() {
    }

    public static String replaceQuebraLinha(String texto) {
        if (Strings.isEmpty(texto)) {
            return texto;
        } else {
            String saida = texto.replace("\\015", "");
            saida = saida.replace("\\012", "");
            saida = saida.replace("\n", "");
            saida = saida.replace("\r", "");
            return saida;
        }
    }

    public static String concatList(List<Object> list, String delimitador) {
        StringBuilder sb = new StringBuilder();
        for (Object object : list) {
            if (sb.length() > 0) {
                sb.append(delimitador);
            }
            sb.append(object);
        }
        return sb.toString();
    }

}
