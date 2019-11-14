package br.com.infox.core.util;

import static br.com.infox.core.util.ObjectUtil.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public final class CollectionUtil {
    private CollectionUtil(){
    }
    
    public static boolean isEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }

    public static <T> T firstOrNull(Collection<T> collection) {
        return is(collection).empty() ? null : collection.iterator().next();
    }
    public static <T> T firstOrNull(Map<?,T> map) {
        return is(map).empty() ? null : map.values().iterator().next();
    }
    public static <T> T firstOrNull(T[] array) {
        return is(array).empty() ? null : array[0];
    }
    public static <T> T lastOrNull(Collection<T> collection) {
        return is(collection).empty() ? null : lastOfCollection(collection);
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
    
    public static <T, E> List<E> castToList(List<T> listSource, final Class<E> clazz) {
        return convertToList(listSource, new ListConversor<T, E>() {
            @Override
            public E convert(T t) {
                return clazz.cast(t);
            }
        });
    }
    
    public static <T, E> List<E> castToList(T[] listSource, final Class<E> clazz) {
        return convertToList(Arrays.asList(listSource), new ListConversor<T, E>() {
            @Override
            public E convert(T t) {
                return clazz.cast(t);
            }
        });
    }
    
    public static interface ListConversor<T, E> {
        
        E convert(T T);   
        
    }

}

