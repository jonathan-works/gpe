package br.com.infox.core.util;

import java.util.Collection;

public final class CollectionUtil {
    private CollectionUtil(){
    }
    
    public static boolean isEmpty(Collection<?> collection){
        return collection == null || collection.isEmpty();
    }
}
