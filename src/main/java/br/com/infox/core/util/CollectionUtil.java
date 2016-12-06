package br.com.infox.core.util;

import java.util.Collection;
import java.util.Iterator;


public final class CollectionUtil {
    private CollectionUtil(){
    }
    
    public static boolean isEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }

    public static <T> T firstOrNull(Collection<T> collection) {
        return isEmpty(collection) ? null : collection.iterator().next();
    }
    public static <T> T lastOrNull(Collection<T> collection) {
        return isEmpty(collection) ? null : lastOfCollection(collection);
    }
    private static <T> T lastOfCollection(Collection<T> collection){
        T result = null;
        for(Iterator<T> iterator=collection.iterator();iterator.hasNext();)
            result = iterator.next();
        
        return result;
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

