package br.com.infox.epp.test.crud;

public final class EntityActionContainer<T> {
    
    private T entity;
    private EntityAction<T> action;
    
    public EntityActionContainer(final T entity, final EntityAction<T> action) {
        if (entity == null) {
            throw new NullPointerException("Entity can't be null");
        }
        this.entity = entity;
        this.action = action;
    }
    
    public T getEntity() {
        return this.entity;
    }
    
    public void run(T entity) {
        this.entity = entity;
        this.action.run(this.entity);
    }
    
}
