package br.com.infox.core.util;

public interface ObjectComparator<T> {

    @SuppressWarnings("unchecked")
    boolean in(T... args);
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

}
