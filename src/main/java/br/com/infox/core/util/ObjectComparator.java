package br.com.infox.core.util;

import java.util.Collection;

public interface ObjectComparator<T> {

    @SuppressWarnings("unchecked")
    boolean in(T... args);
    boolean in (Collection<T> args);
}

class ObjectComparatorImpl<T> implements ObjectComparator<T> {

    private final T obj;

    ObjectComparatorImpl(T obj) {
        this.obj = obj;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean in(T... args) {
        for (T t : args) {
            if (ObjectUtil.equals(obj, t))
                return true;
        }
        return false;
    }

    @Override
    public boolean in(Collection<T> args) {
        if (args == null)
            return false;
        return args.contains(obj);
    }

}
