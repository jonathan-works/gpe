package br.com.infox.epp.test.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static br.com.infox.core.action.AbstractAction.UPDATED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import org.jboss.seam.contexts.TestLifecycle;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.servlet.ServletSessionMap;


public abstract class AbstractCrudTest<T> extends JUnitSeamTest {
    protected final class CrudActionsImpl<E> extends AbstractCrudActions<E> {
        public CrudActionsImpl(final String componentName) {
            super(componentName);
        }
    }
    
    @Deprecated
    protected abstract class InternalRunnableTest<E> extends RunnableTest<E> {
        
        public InternalRunnableTest() {
            super(getComponentName());
        }
        
        public InternalRunnableTest(final String componentName) {
            super(componentName);
        }
        
        public final E runTest() throws Exception {
            return this.runTest(null, null, servletContext, session);
        }
        
        public final E runTest(final E entity) throws Exception {
            return this.runTest(null, entity, servletContext, session);
        }
        
        public final E runTest(final ActionContainer<E> actionContainer) throws Exception {
            return this.runTest(actionContainer, actionContainer.getEntity(), servletContext, session);
        }

        public final E runTest(final ActionContainer<E> actionContainer, final E entity) throws Exception {
            return this.runTest(actionContainer, entity, servletContext, session);
        }

    }

    private static final String ATIVO = "ativo";

    protected static final String SERVLET_3_0 = "Servlet 3.0";

    protected final InternalRunnableTest<T> persistFail = new InternalRunnableTest<T>() {
        @Override
        protected void testComponent() {
            final T entity = getEntity();
            newInstance();
            initEntity(entity, this);
            assertEquals("PERSISTED",false,PERSISTED.equals(save()));
            assertNull("ASSERT NOT NULL ID",getId());
        }
    };

    protected final InternalRunnableTest<T> persistSuccess = new InternalRunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity(); 
            newInstance();
            initEntity(entity, this);
            assertEquals("persisted", PERSISTED, save());

            final Integer id = getId();
            assertNotNull("id", id);
            newInstance();
            assertNull("nullId", getId());
            setId(id);
            assertEquals("Compare", true, compareEntityValues(entity, this));
            setEntity(getInstance());
        }
    };

    protected final InternalRunnableTest<T> inactivateSuccess = new InternalRunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            initEntity(entity, this);
            assertEquals("persisted", PERSISTED, save());
            final Integer id = getId();
            assertNotNull("id not null", id);
            resetInstance(id);
            assertEquals("inactivate", UPDATED, inactivate());
            final T newInstance = resetInstance(id);
            assertEquals("is inactive",Boolean.FALSE, getEntityValue(ATIVO));
            setEntity(newInstance);
        }
    };

    protected final InternalRunnableTest<T> inactivateFail = new InternalRunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            initEntity(entity, this);

            assertEquals("persisted", PERSISTED, save());
            final Integer id = getId();
            assertNotNull("id not null", id);
            resetInstance(id);
            
            assertEquals("updated", false, UPDATED.equals(inactivate()));
            final T instance = resetInstance(id);
            assertEquals("active", Boolean.TRUE,getEntityValue(ATIVO));
            setEntity(instance);
        }
        
    };

    protected final InternalRunnableTest<T> removeSuccess = new InternalRunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            initEntity(entity, this);
            assertEquals("persist", true, PERSISTED.equals(save()));
            assertEquals("id!=null", true, getId() != null);
            assertEquals("remove", true, REMOVED.equals(remove(getInstance())));
        }
    };

    protected final InternalRunnableTest<T> removeFail = new InternalRunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            initEntity(entity, this);
            assertEquals("persist", true, PERSISTED.equals(save()));
            assertEquals("id!=null", true, getId() != null);
            assertEquals("remove", false, REMOVED.equals(remove(getInstance())));
        }
    };
    
    protected final InternalRunnableTest<T> updateSuccess = new InternalRunnableTest<T>() {
        @Override
        public void testComponent() throws Exception {
            final T entity = this.getEntity();
            
            newInstance();
            initEntity(entity, this);
            assertEquals("persisted", PERSISTED, save());

            final Integer id = getId();
            assertNotNull("id", id);
            newInstance();
            assertNull("nullId", getId());
            setId(id);
            assertEquals("Compare", true, compareEntityValues(entity, this));
            
            setEntity(resetInstance(id));
        }
    };
    
    protected final InternalRunnableTest<T> updateFail = new InternalRunnableTest<T>() {
        
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();

            newInstance();
            initEntity(entity, new CrudActionsImpl<T>(getComponentName()));
            assertEquals("persisted", PERSISTED, save());

            final Integer id = getId();
            assertNotNull("id", id);
            newInstance();
            assertNull("nullId", getId());
            setId(id);
            assertEquals("Compare", true, compareEntityValues(entity, this));
            
            setEntity(resetInstance(id));
        }
    };

    protected boolean compareEntityValues(final T entity, final CrudActions<T> crudActions) {
        final Object entityInstance = crudActions.getInstance();
        return entityInstance == entity
                || (entityInstance != null && entity != null);
    }

    protected final boolean compareValues(final Object obj1, final Object obj2) {
        return (obj1 == obj2 || ((obj1 != null) && obj1.equals(obj2)));
    }
    
    protected final void executeTest(final InternalRunnableTest<T> componentTest) throws Exception {
        TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
        try {
            componentTest.runTest();
        } finally {
            TestLifecycle.endTest();
        }
    }
    
    protected final String fillStr(String string, final int topLength) {
        if (string == null || string.length() < 1) {
            string = "-";
        }

        final StringBuilder sb = new StringBuilder(string);
        final int length = string.length();
        if (length < topLength) {
            for (int i = 0, l = topLength - length; i < l; i++) {
                sb.append(string.charAt(0));
            }
        }
        return sb.substring(0, topLength);
    }
    
    protected abstract String getComponentName();
    
    protected abstract void initEntity(T entity, CrudActions<T> crudActions);
}
