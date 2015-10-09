package br.com.infox.core.util;

public class ObjectUtil {
    
    public static boolean equals(Object obj1, Object obj2) {
        return (obj1 == null && obj2 == null) || (obj1 != null && obj1.equals(obj2)) || (obj2 != null && obj2.equals(obj1));
    }

}
