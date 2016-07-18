package br.com.infox.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ArrayUtil {

    private ArrayUtil() {
    }

    public static <E> E[] copyOf(E[] array) {
        if (array != null) {
            return Arrays.copyOf(array, array.length);
        }
        return null;
    }

    public static byte[] copyOf(byte[] array) {
        if (array != null) {
            return Arrays.copyOf(array, array.length);
        }
        return null;
    }
    
    public static <T, E> List<E> convertToList(List<T> listSource, ListConversor<T, E> conversor) {
        if (listSource == null) return null;
        List<E> destination = new ArrayList<E>();
        for (int index = 0 ; index < listSource.size() ; index++) {
            destination.add(index, conversor.convert(listSource.get(index)));
        }
        return destination;
    }
    
    public static interface ListConversor<T, E> {
        
        E convert(T T);   
        
    }
}
