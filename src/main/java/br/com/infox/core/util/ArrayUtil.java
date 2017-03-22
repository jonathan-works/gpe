package br.com.infox.core.util;

import java.util.Arrays;

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
    
}
