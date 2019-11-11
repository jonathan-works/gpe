package br.com.infox.core.util;

public class ELUtil {

    public static boolean isEL(String value) {
        return value != null && (value.startsWith("#{") || value.startsWith("${"));
    }

}
