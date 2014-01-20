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

//    private final CrudActions<T> crudActions = new CrudActions<>(getComponentName());

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

    protected abstract void initEntity(T entity, CrudActions<T> crudActions);

//    protected CrudActions<T> getCrudActions() {
//        return this.crudActions;
//    }

    protected abstract String getComponentName();

    protected final RunnableTest<T> persistFail = new RunnableTest<T>() {
        @Override
        protected void testComponent() {
            final T entity = getEntity();
            crudActions.newInstance();
            initEntity(entity, this.crudActions);
            assertEquals("PERSISTED",false,PERSISTED.equals(crudActions.save()));
            assertNull("ASSERT NOT NULL ID",crudActions.getId());
        }
    };

    protected final RunnableTest<T> persistSuccess = new RunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity(); 
            crudActions.newInstance();
            initEntity(entity, this.crudActions);
            assertEquals("persisted", PERSISTED, crudActions.save());

            final Integer id = crudActions.getId();
            assertNotNull("id", id);
            crudActions.newInstance();
            assertNull("nullId", crudActions.getId());
            crudActions.setId(id);
            assertEquals("Compare", true, compareEntityValues(entity, this.crudActions));
            setEntity(crudActions.getInstance());
        }
    };

    protected final RunnableTest<T> inactivateSuccess = new RunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            crudActions.newInstance();
            initEntity(entity, this.crudActions);
            assert PERSISTED.equals(crudActions.save());
            assert crudActions.getId() != null;
            assert Boolean.TRUE.equals(crudActions.getEntityValue(ATIVO));
            assert UPDATED.equals(crudActions.inactivate());
            assert Boolean.FALSE.equals(crudActions.getEntityValue(ATIVO));
        }
    };

    protected final RunnableTest<T> inactivateFail = new RunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            crudActions.newInstance();
            initEntity(entity, this.crudActions);
            //TODO: Ajustar assertions para retornar mensagens
            assert PERSISTED.equals(crudActions.save());
            assert crudActions.getId() != null;
            assert Boolean.TRUE.equals(crudActions.getEntityValue(ATIVO));
            assert !UPDATED.equals(crudActions.inactivate());
            assert Boolean.TRUE.equals(crudActions.getEntityValue(ATIVO));
        }
        
    };

    protected final RunnableTest<T> removeSuccess = new RunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            crudActions.newInstance();
            initEntity(entity, this.crudActions);
            Assert.assertEquals("persist", true, PERSISTED.equals(crudActions.save()));
            Assert.assertEquals("id!=null", true, crudActions.getId() != null);
            Assert.assertEquals("remove", true, REMOVED.equals(crudActions.remove(crudActions.getInstance())));
        }
    };

    protected final RunnableTest<T> removeFail = new RunnableTest<T>() {
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();
            crudActions.newInstance();
            initEntity(entity, this.crudActions);
            assert crudActions.getId() == null;
            assert PERSISTED.equals(crudActions.save());
            assert REMOVED.equals(crudActions.remove());
        }
    };
    
    protected final RunnableTest<T> updateSuccess = new RunnableTest<T>() {
        @Override
        public void testComponent() throws Exception {
            final T entity = this.getEntity();
            
            crudActions.newInstance();
            initEntity(entity, crudActions);
            assertEquals("persisted", PERSISTED, crudActions.save());

            final Integer id = crudActions.getId();
            assertNotNull("id", id);
            crudActions.newInstance();
            assertNull("nullId", crudActions.getId());
            crudActions.setId(id);
            assertEquals("Compare", true, compareEntityValues(entity, this.crudActions));
            
            executeAction();
            
            assertEquals("UPDATED true ",UPDATED, crudActions.save());
        }
    };
    
    protected final RunnableTest<T> updateFail = new RunnableTest<T>() {
        
        @Override
        protected void testComponent() throws Exception {
            final T entity = getEntity();

            crudActions.newInstance();
            initEntity(entity, new CrudActions<T>(getComponentName()));
            assertEquals("persisted", PERSISTED, crudActions.save());

            final Integer id = crudActions.getId();
            assertNotNull("id", id);
            crudActions.newInstance();
            assertNull("nullId", crudActions.getId());
            crudActions.setId(id);
            assertEquals("Compare", true, compareEntityValues(entity, this.crudActions));
            
            executeAction();
            
            assertEquals("updateFail update", false, UPDATED.equals(crudActions.save())); 
        }
    };

    protected abstract class EntityActionContainer<E> {
        private final E entity;

        public EntityActionContainer(final E entity) {
            if (entity == null) {
                throw new NullPointerException("Null entity not allowed for EntityActionContainer");
            }
            this.entity = entity;
        }

        public abstract void execute(final CrudActions<E> crudActions);

        public E getEntity() {
            return entity;
        }

    }


    protected boolean compareEntityValues(final T entity, final CrudActions<T> crudActions) {
        final Object entityInstance = crudActions.getInstance();
        return entityInstance == entity
                || (entityInstance != null && entity != null);
    }
    
    protected abstract class RunnableTest<E> {
        private E entity;
        private EntityActionContainer<E> actionContainer;
        protected final CrudActions<E> crudActions;
        
        public RunnableTest() {
            this.crudActions = new CrudActions<>(getComponentName());
        }
        
        public RunnableTest(final String componentName) {
            this.crudActions = new CrudActions<>(componentName);
        }
        
        protected abstract void testComponent() throws Exception;
        
        public final void executeAction() {
            if (this.actionContainer != null) {
                this.actionContainer.execute(this.crudActions);
            }
        }
        
        public final E runTest(final E entity) throws Exception {
            this.entity = entity;
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                testComponent();
            } finally {
                TestLifecycle.endTest();
            }
            return this.entity;
        }
        
        public final E runTest(final EntityActionContainer<E> actionContainer) throws Exception {
            this.actionContainer = actionContainer;
            this.entity = actionContainer.getEntity();
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                testComponent();
            } finally {
                TestLifecycle.endTest();
            }
            return this.entity;
        }

        public E getEntity() {
            return entity;
        }

        public void setEntity(E entity) {
            this.entity = entity;
        }
        
    }

    protected final class CrudActions<E> {
        private static final String METHOD_EXPR = "{0}({1}.instance)";
        private static final String INACTIVATE = "inactive";
        private static final String ID = "instanceId";
        private static final String REMOVE = "remove";
        private static final String SAVE = "save";
        private static final String INSTANCE = "instance";
        private static final String NEW_INSTANCE = "newInstance";
        private static final String COMP_EXP = "'#{'{0}.{1}'}'";
        private static final String ENT_EXP = "'#{'{0}.instance.{1}'}'";
        private final String componentName;

        public CrudActions(final String componentName) {
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
            return Expressions.instance().createMethodExpression(format(COMP_EXP, this.componentName, methodName)).invoke(args);
        }

        public final <R> R invokeMethod(final String methodName,
                final Class<R> returnType, final Object... args) {
            final ArrayList<Class<?>> classList = new ArrayList<>();
            for (Object object : args) {
                classList.add(object.getClass());
            }
            final Expressions expressionFactory = Expressions.instance();
            final String expressionString = format(COMP_EXP, this.componentName, methodName);
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
            return (String) this.invokeMethod(SAVE);
        }

        public final String remove() {
            return (String) this.invokeMethod(REMOVE);
        }

        public final Object remove(final E entity) {
            return this.invokeMethod(format(METHOD_EXPR, REMOVE, this.componentName));
        }

        public final Object inactivate() {
            return this.invokeMethod(format(METHOD_EXPR, INACTIVATE, this.componentName));
        }

        public final Integer getId() {
            return (Integer) getComponentValue(ID);
        }

        public final void setId(Object value) {
            setComponentValue(ID, value);
        }

    }
}
