package br.com.infox.epp.test.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static br.com.infox.core.action.AbstractAction.UPDATED;
import static java.text.MessageFormat.format;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;

import junit.framework.Assert;

import org.jboss.seam.contexts.TestLifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.servlet.ServletSessionMap;

import br.com.infox.core.constants.WarningConstants;

public abstract class AbstractGenericCrudTest<T> extends JUnitSeamTest {
    private static final String INACTIVATE = "inactive";
    private static final String ID = "instanceId";
    private static final String REMOVE = "remove";
    private static final String SAVE = "save";
    private static final String INSTANCE = "instance";
    private static final String NEW_INSTANCE = "newInstance";
    private static final String COMP_EXP = "'#{'{0}.{1}'}'";
    private static final String COMP_METHOD_EXP = "'#{'{0}.{1}'}'";
    private static final String ENT_EXP = "'#{'{0}.instance.{1}'}'";
    private static final String ATIVO = "ativo";
    protected static final String SERVLET_3_0 = "Servlet 3.0";

    protected final String fillStr(String string, final int topLength) {
        if (string == null || string.length() < 1) {
            string = "-";
        }

        final StringBuilder sb = new StringBuilder(string);
        int length = string.length();
        if (length < topLength) {
            for (int i = 0, l = topLength - length; i < l; i++) {
                sb.append(string.charAt(0));
            }
        }
        return sb.substring(0, topLength);
    }

    protected final void executeTest(final Runnable componentTest) throws Exception {
        TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
        try {
            componentTest.run();
        } finally {
            TestLifecycle.endTest();
        }
    }
    
    protected final boolean compareValues(final Object obj1, final Object obj2) {
        return (obj1 == obj2 || ((obj1 != null) && obj1.equals(obj2)));
    }

    protected abstract void initEntity(T entity, ICrudActions<T> crudActions);

    protected abstract String getComponentName();

    protected final RunnableTest<T> persistFail = new RunnableTest<T>() {
        @Override
        protected void testComponent() {
            final T entity = getEntity();
            newInstance();
            initEntity(entity, this);
            assertEquals("PERSISTED",false,PERSISTED.equals(save()));
            assertNull("ASSERT NOT NULL ID",getId());
        }
    };

    protected final RunnableTest<T> persistSuccess = new RunnableTest<T>() {
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

    protected final RunnableTest<T> inactivateSuccess = new RunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            initEntity(entity, this);
            assertEquals("persisted", PERSISTED, save());
            final Integer id = getId();
            assertNotNull("id not null", id);
            resetInstance(id);
            assertEquals("is active", Boolean.TRUE, getEntityValue(ATIVO));
            assertEquals("inactivate", UPDATED, inactivate());
            assertEquals("is inactive",Boolean.FALSE, getEntityValue(ATIVO));
            setEntity(resetInstance(id));
        }
    };

    protected final RunnableTest<T> inactivateFail = new RunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            initEntity(entity, this);

            assertEquals("persisted", PERSISTED, save());
            final Integer id = getId();
            assertNotNull("id not null", id);
            resetInstance(id);
            
            assertEquals("active", Boolean.TRUE,getEntityValue(ATIVO));
            assertEquals("updated", false, UPDATED.equals(inactivate()));
            assertEquals("active", Boolean.TRUE,getEntityValue(ATIVO));
            setEntity(resetInstance(id));
        }
        
    };

    protected final RunnableTest<T> removeSuccess = new RunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            initEntity(entity, this);
            Assert.assertEquals("persist", true, PERSISTED.equals(save()));
            Assert.assertEquals("id!=null", true, getId() != null);
            Assert.assertEquals("remove", true, REMOVED.equals(remove(getInstance())));
        }
    };

    protected final RunnableTest<T> removeFail = new RunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            newInstance();
            initEntity(entity, this);
            assert getId() == null;
            assert PERSISTED.equals(save());
            assert REMOVED.equals(remove());
        }
    };
    
    protected final RunnableTest<T> updateSuccess = new RunnableTest<T>() {
        @Override
        public void testComponent() throws Exception {
            final T entity = this.getEntity();
            
            newInstance();
            initEntity(entity, crudActions);
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
    
    protected final RunnableTest<T> updateFail = new RunnableTest<T>() {
        
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();

            newInstance();
            initEntity(entity, new CrudActions<T>(getComponentName()));
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

    protected abstract class EntityActionContainer<E> {
        private E entity;

        public EntityActionContainer() {
            entity = null;
        }
        
        public EntityActionContainer(final E entity) {
            if (entity == null) {
                throw new NullPointerException("Null entity not allowed for EntityActionContainer");
            }
            this.entity = entity;
        }

        public abstract void execute(final ICrudActions<E> crudActions);

        public E getEntity() {
            return entity;
        }
    }

    protected boolean compareEntityValues(final T entity, final ICrudActions<T> crudActions) {
        final Object entityInstance = crudActions.getInstance();
        return entityInstance == entity
                || (entityInstance != null && entity != null);
    }
    
    protected abstract class RunnableTest<E> extends AbstractCrudActions<E>{
        private E entity;
        private EntityActionContainer<E> actionContainer;
        protected final ICrudActions<E> crudActions;
        //private HttpSession session;
        //private ServletContext servletContext;
        
        public RunnableTest() {
            super(getComponentName());
            this.crudActions = this;
            //this.session = AbstractGenericCrudTest.super.session;
            //this.servletContext = AbstractGenericCrudTest.super.servletContext;
        }
        
        public RunnableTest(final String componentName) {
            super(componentName);
            this.crudActions = this;
            //this.session = AbstractGenericCrudTest.super.session;
            //this.servletContext = AbstractGenericCrudTest.super.servletContext;
        }
        
        protected abstract void testComponent() throws Exception;
        
        public final E runTest() throws Exception {
            return this.runTest(null, null);
        }
        
        public final E runTest(final E entity) throws Exception {
            return this.runTest(null, entity);
        }
        
        public final E runTest(final EntityActionContainer<E> actionContainer) throws Exception {
            return this.runTest(actionContainer, actionContainer.getEntity());
        }
        
        public final E runTest(final EntityActionContainer<E> actionContainer, final E entity) throws Exception {
            this.entity = entity;
            this.actionContainer = actionContainer;
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                testComponent();
                if (this.actionContainer != null) {
                    this.actionContainer.entity = entity;
                    this.actionContainer.execute(this);
                }
            } finally {
                TestLifecycle.endTest();
            }
            return this.entity;
        }

        public final E getEntity() {
            return entity;
        }

        public final void setEntity(E entity) {
            this.entity = entity;
        }
    }
    
    protected final class CrudActions<E> extends AbstractCrudActions<E> {
        public CrudActions(final String componentName) {
            super(componentName);
        }
    }
    
    private abstract class AbstractCrudActions<E> implements ICrudActions<E> {
        private final String componentName;

        public AbstractCrudActions(final String componentName) {
            this.componentName = componentName;
        }

        public final void setEntityValue(final String field, final Object value) {
            final String valueExpression = format(ENT_EXP, this.componentName, field);
            createValueExpression(valueExpression).setValue(value);
        }

        private ValueExpression<Object> createValueExpression(
                final String valueExpression) {
            return Expressions.instance().createValueExpression(valueExpression);
        }

        @SuppressWarnings(WarningConstants.UNCHECKED)
        public final <R> R getEntityValue(final String field) {
            final String valueExpression = format(ENT_EXP, this.componentName, field);
            return (R) createValueExpression(valueExpression).getValue();
        }

        public final void setComponentValue(final String field,
                final Object value) {
            final String valueExpression = format(COMP_EXP, this.componentName, field);
            createValueExpression(valueExpression).setValue(value);
        }

        @SuppressWarnings(WarningConstants.UNCHECKED)
        public final <R> R getComponentValue(final String field) {
            final String valueExpression = format(COMP_EXP, this.componentName, field);
            return (R) createValueExpression(valueExpression).getValue();
        }

        public final Object invokeMethod(final String methodName,
                final Object... args) {
            return Expressions.instance().createMethodExpression(format(COMP_METHOD_EXP, this.componentName, methodName)).invoke(args);
        }

        public final <R> R invokeMethod(final String methodName, final Class<R> returnType, final Class<?>[] paramTypes, final Object...args) {
            final ArrayList<Class<?>> classList = new ArrayList<>();
            for (Object object : args) {
                classList.add(object.getClass());
            }
            final Expressions expressionFactory = Expressions.instance();
            final String expressionString = format(COMP_METHOD_EXP, this.componentName, methodName);
            return expressionFactory.createMethodExpression(expressionString, returnType, paramTypes).invoke(args);
        }

        public final <R> R invokeMethod(final String methodName,
                final Class<R> returnType, final Object... args) {
            final ArrayList<Class<?>> classList = new ArrayList<>();
            for (Object object : args) {
                classList.add(object.getClass());
            }
            final Expressions expressionFactory = Expressions.instance();
            final String expressionString = format(COMP_METHOD_EXP, this.componentName, methodName);
            final Class<?>[] types = classList.toArray(new Class<?>[classList.size()]);
            return expressionFactory.createMethodExpression(expressionString, returnType, types).invoke(args);
        }

        public final void newInstance() {
            this.invokeMethod(NEW_INSTANCE);
        }

        public final E createInstance() {
            this.newInstance();
            return this.getInstance();
        }
        
        public final E resetInstance(Object id) {
            this.newInstance();
            this.setId(id);
            return this.getInstance();
        }
        
        @SuppressWarnings(WarningConstants.UNCHECKED)
        public final E getInstance() {
            return (E) getComponentValue(INSTANCE);
        }

        public void setInstance(final E value) {
            setComponentValue(INSTANCE, value);
        }

        public final String save() {
            return this.invokeMethod(SAVE, String.class);
        }

        public final String remove() {
            return this.invokeMethod(REMOVE, String.class);
        }

        public final String remove(final E entity) {
            final Class<?>[] paramTypes = {Object.class};
            return this.invokeMethod(REMOVE, String.class, paramTypes, entity);
        }

        public final String inactivate() {
            final Class<?>[] paramTypes = {Object.class};
            return this.invokeMethod(INACTIVATE, String.class, paramTypes, getInstance());
        }

        public final Integer getId() {
            return getComponentValue(ID);
        }

        public final void setId(Object value) {
            setComponentValue(ID, value);
        }

    }
}
