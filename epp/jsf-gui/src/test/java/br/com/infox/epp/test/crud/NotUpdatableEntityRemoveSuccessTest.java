package br.com.infox.epp.test.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static org.junit.Assert.assertEquals;

public abstract class NotUpdatableEntityRemoveSuccessTest<E> extends RunnableTest<E> {

    private ActionContainer<E> initEntity;

    public NotUpdatableEntityRemoveSuccessTest(final String componentName, final ActionContainer<E> initEntity) {
        super(componentName);
        this.initEntity = initEntity;
    }

    @Override
    protected void testComponent() throws Exception {
        final E entity = getEntity();
        newInstance();
        this.initEntity.setEntity(entity);
        this.initEntity.execute(this);
        assertEquals("persist", true, PERSISTED.equals(save()));
        assertEquals("managed", true, checkManagedEntity());
        assertEquals("remove", true, REMOVED.equals(remove(getInstance())));
    }

    protected abstract boolean checkManagedEntity();
}
