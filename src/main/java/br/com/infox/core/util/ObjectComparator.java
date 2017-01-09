package br.com.infox.core.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public interface ObjectComparator<T> {

    @SuppressWarnings("unchecked")
    boolean in(T... args);
    boolean in (Collection<T> args);

    boolean empty();

    boolean notEmpty();
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

    public boolean empty() {
        if (obj == null)
            return true;

        if (obj instanceof String)
            return ((String) obj).trim().isEmpty();

        if (obj instanceof Collection)
            return ((Collection<?>) obj).isEmpty();

        if (obj instanceof Map)
            return ((Map<?, ?>) obj).isEmpty();

        if (obj.getClass().isArray())
            return Arrays.asList(obj).isEmpty();

        return false;
    }

    public boolean notEmpty() {
        return !empty();
    }
}
