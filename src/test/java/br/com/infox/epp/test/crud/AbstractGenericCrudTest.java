package br.com.infox.epp.test.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static br.com.infox.core.action.AbstractAction.UPDATED;
import static java.text.MessageFormat.format;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.jboss.seam.contexts.TestLifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.servlet.ServletSessionMap;
import org.junit.Test;

import br.com.infox.core.constants.WarningConstants;
import br.com.infox.core.crud.Crudable;

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
            for (int i = 0, l = topLength-length; i < l; i++) {
                sb.append(string.charAt(0));
            }
        }
        return sb.substring(0, topLength);
    }
    
    private final CrudActions<T> crudActions = new CrudActions<>(getComponentName());
    
    /**
     * Call a method binding
     */
    protected final Object invokeMethod(final String methodExpression, final Object... args) {
        return Expressions.instance().createMethodExpression(methodExpression).invoke(args);
    }

    /**
     * Evaluate (get) a value binding
     */
    protected final Object getValue(final String valueExpression) {
        return Expressions.instance().createValueExpression(valueExpression).getValue();
    }

    /**
     * Set a value binding
     */
    protected final void setValue(final String valueExpression, final Object value) {
        Expressions.instance().createValueExpression(valueExpression).setValue(value);
    }

    protected final void executeTest(final Runnable componentTest) {
        TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
        try {
            componentTest.run();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            TestLifecycle.endTest();
        }
    }

    protected List<T> getPersistSuccessList() {
        return null;
    }
    protected List<T> getPersistFailList() {
        return null;
    }
    protected List<T> getInactivateSuccessList() {
        return null;
    }
    protected List<T> getInactivateFailList() {
        return null;
    }
    
    protected List<EntityActionContainer<T>> getUpdateSuccessList() {
        return null;
    }
    
    protected List<EntityActionContainer<T>> getUpdateFailList() {
        return null;
    }
    
    protected List<T> getRemoveSuccessList() {
        return null;
    }
    
    protected List<T> getRemoveFailList() {
        return null;
    }

    @Test
    public final void initPersistSuccessTest() {
        final List<T> list = getPersistSuccessList();
        if (list != null) {
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                for (final T entity : list) {
                        persistSuccessTest(entity);
                }
            } catch (Exception e) {
                throw new AssertionFailedError(e.getMessage());
            } finally {
                TestLifecycle.endTest();
            }
        }
    }
    
    protected boolean compareEntityValues(final T entity) {
        final Object entityInstance = crudActions.getInstance();
        return entityInstance == entity || (entityInstance != null && entityInstance.equals(entity));
    }

    protected abstract void initEntity(T entity);

    @Test
    public final void initRemoveSuccessTest() {
        final List<T> list = getRemoveSuccessList();
        if (list != null) {
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                for (final T entity : list) {
                        removeSuccessTest(entity);
                }
            } catch (Exception e) {
                throw new AssertionFailedError(e.getMessage());
            } finally {
                TestLifecycle.endTest();
            }
        }
    }

    @Test
    public final void initUpdateSuccessTest() {
        final List<EntityActionContainer<T>> list = getUpdateSuccessList();
        if (list != null) {
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                for (final EntityActionContainer<T> entityActionContainer : list) {
                    updateSuccessTest(entityActionContainer);
                }
            } catch (Exception e) {
                throw new AssertionFailedError(e.getMessage());
            } finally {
                TestLifecycle.endTest();
            }
        }
    }

    @Test
    public final void initInactivateSuccessTest() {
        final List<T> list = getInactivateSuccessList();
        if (list != null) {
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                for (final T entity : list) {
                        this.inactivateSuccessTest(entity);
                }
            } catch (Exception e) {
                throw new AssertionFailedError(e.getMessage());
            } finally {
                TestLifecycle.endTest();
            }
        }
    }

    @Test
    public final void initPersistFailTest() {
        final List<T> list = getPersistFailList();
        if (list != null) {
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                for (final T entity : list) {
                        persistFailTest(entity);
                }
            } catch (Exception e) {
                throw new AssertionFailedError(e.getMessage());
            } finally {
                TestLifecycle.endTest();
            }
        }
    }

    @Test
    public final void initRemoveFailTest() {
        final List<T> list = getRemoveFailList();
        if (list != null) {
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                for (final T entity : list) {
                        removeFailTest(entity);
                }
            } catch (Exception e) {
                throw new AssertionFailedError(e.getMessage());
            } finally {
                TestLifecycle.endTest();
            }
        }
    }

    @Test
    public final void initUpdateFailTest() {
        final List<EntityActionContainer<T>> list = getUpdateFailList();
        if (list != null) {
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                for (final EntityActionContainer<T> entityActionContainer : list) {
                    updateFailTest(entityActionContainer);
                }
            } catch (Exception e) {
                throw new AssertionFailedError(e.getMessage());
            } finally {
                TestLifecycle.endTest();
            }
        }
    }

    @Test
    public final void initInactivateFailTest() {
        final List<T> list = getInactivateFailList();
        if (list != null) {
            try {
                TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
                for (final T entity : list) {
                        inactivateFailTest(entity);
                }
            } catch (Exception e) {
                throw new AssertionFailedError(e.getMessage());
            } finally {
                TestLifecycle.endTest();
            }
        }
    }

    protected CrudActions<T> getCrudActions() {
        return this.crudActions;
    }

    protected abstract String getComponentName();

    protected void persistFailTest(final T entity) {
        crudActions.newInstance();
        initEntity(entity);
        assert !PERSISTED.equals(crudActions.save());
        Object id = crudActions.getId();
        assertNull(id);
    }
    
    protected void persistSuccessTest(final T entity) {      
        crudActions.newInstance();
        initEntity(entity);
        final Object persistResult = crudActions.save();
        assertEquals(PERSISTED, persistResult);
        
        Integer id = crudActions.getId();
        assertNotNull(id);
        crudActions.newInstance();
        Integer nullId = crudActions.getId();
        assertNull(nullId);
        crudActions.setId(id);
        assert compareEntityValues(entity);
    }

    protected void inactivateSuccessTest(final T entity) {
        crudActions.newInstance();
        initEntity(entity);
        assert PERSISTED.equals(crudActions.save());
        assert crudActions.getId() != null;
        assert Boolean.TRUE.equals(crudActions.getEntityValue(ATIVO));
        assert UPDATED.equals(crudActions.inactivate());
        assert Boolean.FALSE.equals(crudActions.getEntityValue(ATIVO));
    }

    protected void inactivateFailTest(final T entity) {
        crudActions.newInstance();
        initEntity(entity);
        assert PERSISTED.equals(crudActions.save());
        assert crudActions.getId() != null;
        assert Boolean.TRUE.equals(crudActions.getEntityValue(ATIVO));
        assert !UPDATED.equals(crudActions.inactivate());
        assert Boolean.TRUE.equals(crudActions.getEntityValue(ATIVO));
    }
    
    protected void removeSuccessTest(final T entity) {
        crudActions.newInstance();
        initEntity(entity);
        assert PERSISTED.equals(crudActions.save());
        assert crudActions.getId() != null;
        assert REMOVED.equals(crudActions.remove());
    }
    
    protected void removeFailTest(final T entity) {
        crudActions.newInstance();
        initEntity(entity);
        assert crudActions.getId() == null;
        assert PERSISTED.equals(crudActions.save());
        assert REMOVED.equals(crudActions.remove());
    }
    
    protected void updateSuccessTest(final EntityActionContainer<T> entityActionContainer) {
        crudActions.newInstance();
        initEntity(entityActionContainer.getEntity());
        assert PERSISTED.equals(crudActions.save());
        final Integer id = crudActions.getId();
        assert id != null;
        
        crudActions.newInstance();
        crudActions.setId(id);
        entityActionContainer.execute();
        assert UPDATED.equals(crudActions.save());
        crudActions.newInstance();
        crudActions.setId(id);
        assert !compareEntityValues(entityActionContainer.getEntity());
    }
    
    protected void updateFailTest(final EntityActionContainer<T> entityActionContainer) {
        crudActions.newInstance();
        initEntity(entityActionContainer.getEntity());
        assert PERSISTED.equals(crudActions.save());
        final Integer id = crudActions.getId();
        assert id != null;
        entityActionContainer.execute();
        assert !UPDATED.equals(crudActions.save());
        crudActions.newInstance();
        crudActions.setId(id);
        assert compareEntityValues(entityActionContainer.getEntity());
    }
    
    public abstract class EntityActionContainer<E> {
        private final E entity;
        
        public EntityActionContainer(final E entity) {
            if (entity == null) {
                throw new NullPointerException("Null entity not allowed for EntityActionContainer");
            }
            this.entity = entity;
        }
        
        public abstract void execute();

        public E getEntity() {
            return entity;
        }
        
    }
    
    public final class CrudActions<E> implements Crudable<E> {
        private static final String INACTIVATE = "inactive";
        private static final String ID = "id";
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

        private ValueExpression<Object> createValueExpression(final String valueExpression) {
            return Expressions.instance().createValueExpression(valueExpression);
        }
        
        @SuppressWarnings(WarningConstants.UNCHECKED)
        public final <R> R getEntityValue(final String field) {
            final String valueExpression = format(ENT_EXP, this.componentName, field);
            return (R) createValueExpression(valueExpression).getValue();
        }
        
        public final void setComponentValue(final String field, final Object value) {
            final String valueExpression = format(COMP_EXP, this.componentName, field);
            createValueExpression(valueExpression).setValue(value);
        }
        
        @SuppressWarnings(WarningConstants.UNCHECKED)
        public final <R> R getComponentValue(final String field) {
            final String valueExpression = format(COMP_EXP, this.componentName, field);
            return (R) createValueExpression(valueExpression).getValue();
        }
        
        public final Object invokeMethod(final String methodName, final Object... args) {
            return Expressions.instance().createMethodExpression(format(COMP_EXP, this.componentName, methodName)).invoke(args);
        }
        
        public final <R> R invokeMethod(final String methodName, final Class<R> returnType, final Object... args) {
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
        
        public final String remove(final E entity) {
            return this.invokeMethod(REMOVE, String.class, entity);
        }
        
        public final String inactivate() {
            return this.invokeMethod(INACTIVATE, String.class, this.getInstance());
        }
        
        public final Integer getId() {
            return (Integer) getComponentValue(ID);
        }

        public final void setId(Object value) {
            setComponentValue(ID, value);
        }

        @Override
        public String getTab() {
            return (String) this.getComponentValue("tab");
        }

        @Override
        public void setTab(final String value) {
            this.setComponentValue("tab", value);
        }

        @Override
        public boolean isManaged() {
            return this.invokeMethod("isManaged", Boolean.class);
        }
    }        
}