package br.com.infox.jsf.function;

import java.util.Collections;
import java.util.List;

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
    
    @SuppressWarnings("rawtypes")
    public static List truncateList(List list, int first, int ammount){
        /*
         * Quando especificar ammount 0, deve retornar uma lista vazia
         * Quando especificar ammount maior que o tamanho da lista, deve considerar que vai copiar a lista até o último nó
         * Quando o first for menor que 0 deve dar index out of bounds exception 
         */
        if (list == null || ammount < 1) 
            return Collections.emptyList();
        int size = list.size();
        int fromIndex = Math.min(first, size);
        int toIndex = Math.min(fromIndex+ammount, size);
        if (toIndex > size || fromIndex > toIndex)
            return Collections.emptyList();
        
        return list.subList(fromIndex, toIndex);
    }

}
