package br.com.infox.core.util;

import java.util.Collection;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.jboss.seam.util.Strings;

import br.com.infox.seam.exception.BusinessException;

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

    public static <E> String concatList(Collection<E> list, String delimitador) {
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
    
    public static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length();
            index = builder.indexOf(from, index);
        }
    }

    public static String encodeToUrlSafeString(String string) {
        try {
            return URLEncoder.encode(string, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException("StringUtil.encodeToUrlSafeString",e);
        }
    }
    public static String decodeFromUrlSafeString(String string){
        try {
            return URLDecoder.decode(string, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new BusinessException("StringUtil.decodeFromUrlSafeString",e);
        }
    }
    
}
