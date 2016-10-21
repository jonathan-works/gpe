package br.com.infox.core.util;

import java.util.Collection;


public final class CollectionUtil {
    private CollectionUtil(){
    }
    
    public static boolean isEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }

    public static <T> T firstOrNull(Collection<T> collection) {
        return isEmpty(collection) ? null : collection.iterator().next();
    }
    
    public static boolean hasAtLeast(Collection<?> collection, int ammount){
        if (ammount < 0) 
            return false;
        if (ammount == 0){
            return isEmpty(collection) || hasAtLeast(collection, 1);
        }
        return collection != null && collection.size() >= ammount;
    }
    
}

