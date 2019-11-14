package br.com.infox.core.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class ObjectUtil {
    
    public static boolean equals(Object obj1, Object obj2) {
        return (obj1 == null && obj2 == null) || (obj1 != null && obj1.equals(obj2)) || (obj2 != null && obj2.equals(obj1));
    }
    
    public static <T> ObjectComparator<T> is(T obj){
        return new ObjectComparatorImpl<T>(obj);
    }
    
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        else if (value instanceof String) {
            return isEmpty((String) value);
        }
        else if (value instanceof Collection) {
            return isEmpty((Collection<?>) value);
        }
        else if (value instanceof Map) {
            return isEmpty((Map<?, ?>) value);
        }
        else if (value.getClass().isArray()) {
            return Array.getLength(value) == 0;
        }
        else {
            return value.toString() == null || value.toString().isEmpty();
        }
    }
    
    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

}
