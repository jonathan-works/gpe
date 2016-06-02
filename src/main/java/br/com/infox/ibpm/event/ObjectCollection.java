package br.com.infox.ibpm.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class ObjectCollection implements Collection<Object> {
    
    private Collection<Object> list;
    private Class<?> type;
    
    public ObjectCollection() {
        list = new ArrayList<>();
    }
    
    public ObjectCollection(Class<?> type) {
        this.type = type;
        list = new ArrayList<>();
    }
    
    public ObjectCollection(Collection<Object> collection) {
        list = collection;
    }
    
    public ObjectCollection put(Object object) {
        add(object);
        return this;
    }
    
    public ObjectCollection put(Collection<Object> objectList) {
        if (objectList != null) {
            for (Object object : objectList) {
                add(object);
            }
        }
        return this;
    }
    
    private boolean canAdd(Object object) {
        if (object == null) return false;
        if (type != null) {
            return type.isAssignableFrom(object.getClass());
        } else {
            return true;
        }
    }
    
    @Override
    public boolean add(Object e) {
        return canAdd(e) && list.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        return list.addAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public Iterator<Object> iterator() {
        return list.iterator();
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }
    
}
