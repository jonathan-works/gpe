package br.com.infox.jsf.function;

public final class JsfFunctions {

    private JsfFunctions() {
    }

    public static Object get(Object value, Object defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Integer splitLength(String obj, String token) {
        if (obj == null) {
            return 0;
        }
        return obj.split(token).length;
    }

}
