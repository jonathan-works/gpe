package br.com.infox.core.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


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
    
    public static <T, E> List<E> convertToList(List<T> listSource, ListConversor<T, E> conversor) {
        if (listSource == null) return null;
        List<E> destination = new ArrayList<E>();
        for (int index = 0 ; index < listSource.size() ; index++) {
            destination.add(index, conversor.convert(listSource.get(index)));
        }
        return destination;
    }
    
    public static <T, E> void convertTo(Collection<T> listSource, Collection<E> destination, ListConversor<T, E> conversor) {
        if (listSource == null) return;
        for (T objectT : listSource) {
            destination.add(conversor.convert(objectT));
        }
    }
    
    public static interface ListConversor<T, E> {
        
        E convert(T T);   
        
    }

}

