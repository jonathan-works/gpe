package br.com.infox.epp.test.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class PersistSuccessTest<E> extends RunnableTest<E>{

    private final ActionContainer<E> initEntity;
    
    public PersistSuccessTest(final String componentName, final ActionContainer<E> initEntity) {
        super(componentName);
        this.initEntity = initEntity;
    }

    @Override
    protected void testComponent() throws Exception {
        final E entity = getEntity(); 
        newInstance();
        this.initEntity.setEntity(entity);
        this.initEntity.execute(this);
        assertEquals("persisted", PERSISTED, save());

        final Integer id = getId();
        assertNotNull("id", id);
        newInstance();
        assertNull("nullId", getId());
        setId(id);
        setEntity(getInstance());
    }

}
