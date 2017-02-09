package br.com.infox.epp.test.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static br.com.infox.core.action.AbstractAction.UPDATED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.jboss.seam.contexts.TestLifecycle;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.servlet.ServletSessionMap;

import br.com.infox.epp.test.crud.RunnableTest.ActionContainer;


public abstract class AbstractCrudTest<T> extends JUnitSeamTest {
    protected final class CrudActionsImpl<E> extends AbstractCrudActions<E> {
        public CrudActionsImpl(final String componentName) {
            super(componentName);
        }
    }

    private static final String ATIVO = "ativo";

    protected static final String SERVLET_3_0 = "Servlet 3.0";

    protected final RunnableTest<T> persistFail = new RunnableTest<T>(getComponentName()) {
        @Override
        protected void testComponent() {
            final T entity = getEntity();
            newInstance();
            getInitEntityAction().setEntity(entity);
            getInitEntityAction().execute(this);
            assertEquals("PERSISTED",false,PERSISTED.equals(save()));
            assertNull("ASSERT NOT NULL ID",getId());
        }
    };

    protected final RunnableTest<T> persistSuccess = new RunnableTest<T>(getComponentName()) {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity(); 
            newInstance();
            getInitEntityAction().setEntity(entity);
            getInitEntityAction().execute(this);
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

    protected final RunnableTest<T> inactivateSuccess = new RunnableTest<T>(getComponentName()) {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            getInitEntityAction().setEntity(entity);
            getInitEntityAction().execute(this);
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

    protected final RunnableTest<T> inactivateFail = new RunnableTest<T>(getComponentName()) {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            getInitEntityAction().setEntity(entity);
            getInitEntityAction().execute(this);

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

    protected final RunnableTest<T> removeSuccess = new RunnableTest<T>(getComponentName()) {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            getInitEntityAction().setEntity(entity);
            getInitEntityAction().execute(this);
            assertEquals("persist", true, PERSISTED.equals(save()));
            assertEquals("id!=null", true, getId() != null);
            assertEquals("remove", true, REMOVED.equals(remove(getInstance())));
        }
    };

    protected final RunnableTest<T> removeFail = new RunnableTest<T>(getComponentName()) {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            getInitEntityAction().setEntity(entity);
            getInitEntityAction().execute(this);
            assertEquals("persist", true, PERSISTED.equals(save()));
            assertEquals("id!=null", true, getId() != null);
            assertEquals("remove", false, REMOVED.equals(remove(getInstance())));
        }
    };
    
    protected final RunnableTest<T> updateSuccess = new RunnableTest<T>(getComponentName()) {
        @Override
        public void testComponent() throws Exception {
            final T entity = this.getEntity();
            
            newInstance();
            getInitEntityAction().setEntity(entity);
            getInitEntityAction().execute(this);
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
    
    protected final RunnableTest<T> updateFail = new RunnableTest<T>(getComponentName()) {
        
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();

            newInstance();
            getInitEntityAction().setEntity(entity);
            getInitEntityAction().execute(this);
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
    
    protected final void executeTest(final RunnableTest<T> componentTest) throws Exception {
        TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
        try {
            componentTest.runTest(servletContext, session);
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
    
    protected ActionContainer<T> getInitEntityAction() {
        return null;
    }
}
