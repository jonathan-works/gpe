package br.com.infox.core.util;

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
    
    public static String preencherComZerosAEsquerda(String string, int length) {
    	int times = length - string.trim().length();
		for (;times > 0; times--) {
			string = "0" + string;
		}
    	return string;
    }

    public static <E> String concatList(List<E> list, String delimitador) {
        StringBuilder sb = new StringBuilder();
        for (E object : list) {
            if (sb.length() > 0) {
                sb.append(delimitador);
            }
            sb.append(object);
        }
        return sb.toString();
    }
    
    public static boolean isEmpty(String value) {
    	return value == null || value.trim().length() == 0;
    }
    
}
