package br.com.infox.epp.test.crud;

import static br.com.infox.core.action.AbstractAction.PERSISTED;
import static br.com.infox.core.action.AbstractAction.REMOVED;
import static br.com.infox.core.action.AbstractAction.UPDATED;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

import java.util.List;

import junit.framework.Assert;
import junit.framework.AssertionFailedError;

import org.jboss.seam.contexts.TestLifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.servlet.ServletSessionMap;
import org.junit.Test;

//import br.com.infox.core.action.AbstractAction;

public abstract class AbstractGenericCrudTest<T> extends JUnitSeamTest {
    private static final String ATIVO = "ativo";
    protected static final String SERVLET_3_0 = "Servlet 3.0";
    
    protected final String fillStr(String string, final int topLength) {
        if (string == null || string.length() < 1) {
            string = "-";
        }
        
        StringBuilder sb = new StringBuilder(string);
        int length = string.length();
        if (length < topLength) {
            for (int i = 0, l = topLength-length; i < l; i++) {
                sb.append(string.charAt(0));
            }
        }
        return sb.substring(0, topLength);
    }
    
    /**
     * Call a method binding
     */
    protected final Object invokeMethod(final String methodExpression, final Object... args) {
        return Expressions.instance().createMethodExpression(methodExpression).invoke(args);
    }

    protected final Object invokeMethod(final String componentName, final String methodName, Object... args) {
        final String expression = new StringBuilder().append("#{").append(componentName).append(".").append(methodName).append("}").toString();
        return invokeMethod(expression, args);
    }

    /**
     * Evaluate (get) a value binding
     */
    protected final Object getValue(final String valueExpression) {
        return Expressions.instance().createValueExpression(valueExpression).getValue();
    }

    protected final Object getComponentValue(final String componentName, final String fieldName) {
        final String valueExpression = new StringBuilder().append("#{").append(componentName).append(".").append(fieldName).append("}").toString();
        return Expressions.instance().createValueExpression(valueExpression).getValue();
    }
    
    protected final Object getValue(final String componentName, final String fieldName) {
        final String valueExpression = new StringBuilder().append("#{").append(componentName).append(".instance.").append(fieldName).append("}").toString();
        return Expressions.instance().createValueExpression(valueExpression).getValue();
    }

    /**
     * Set a value binding
     */
    protected final void setValue(final String valueExpression, final Object value) {
        Expressions.instance().createValueExpression(valueExpression).setValue(value);
    }

    protected final void setComponentValue(final String componentName, final String fieldName, final Object value) {
        final String valueExpression = new StringBuilder().append("#{").append(componentName).append(".").append(fieldName).append("}").toString();
        Expressions.instance().createValueExpression(valueExpression).setValue(value);
    }

    protected final void setValue(final String componentName, final String fieldName, final Object value) {
        final String valueExpression = new StringBuilder().append("#{").append(componentName).append(".instance.").append(fieldName).append("}").toString();
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
        final Object entityInstance = getInstance();
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

    protected void setEntityValue(final String fieldName, final Object codigoDocumento) {
        setValue(getComponentName(), fieldName, codigoDocumento);
    }
    
    protected Object getEntityValue(final String fieldName) {
        return getValue(getComponentName(), fieldName);
    }

    protected abstract String getComponentName();

    protected void newInstance() {
        invokeMethod(getComponentName(), "newInstance");
    }
    
    protected Object getInstance() {
        return getComponentValue(getComponentName(), "instance");
    }
    
    protected Object save() {
        return invokeMethod(getComponentName(), "save");
    }
    
    protected Object remove() {
        return invokeMethod(getComponentName(), "remove");
    }
    
    protected Object inactivate() {
        return invokeMethod("#{"+getComponentName()+".inactive("+getComponentName()+".instance)}");
    }
    
    protected Object getId() {
        return getComponentValue(getComponentName(), "id");
    }

    protected void setId(Object value) {
        setComponentValue(getComponentName(), "id", value);
    }

    private void persistFailTest(final T entity) {
        newInstance();
        initEntity(entity);
        
        assert !PERSISTED.equals(save());
        Object id = getId();
        assertNull(id);
    }
    
    private void persistSuccessTest(final T entity) {
        newInstance();
        initEntity(entity);
        final Object persistResult = save();
        assertEquals(PERSISTED, persistResult);
        
        Object id = getId();
        assertNotNull(id);
        newInstance();
        Object nullId = getId();
        assertNull(nullId);
        setId(id);
        assert compareEntityValues(entity);
    }

    private void inactivateSuccessTest(final T entity) {
        newInstance();
        initEntity(entity);
        assert PERSISTED.equals(save());
        assert getId() != null;
        assert Boolean.TRUE.equals(getEntityValue(ATIVO));
        assert UPDATED.equals(inactivate());
        assert Boolean.FALSE.equals(getEntityValue(ATIVO));
    }
    
    private void inactivateFailTest(final T entity) {
        newInstance();
        initEntity(entity);
        assert PERSISTED.equals(save());
        assert getId() != null;
        assert Boolean.TRUE.equals(getEntityValue(ATIVO));
        assert !UPDATED.equals(inactivate());
        assert Boolean.TRUE.equals(getEntityValue(ATIVO));
    }
    
    private void removeSuccessTest(final T entity) {
        newInstance();
        initEntity(entity);
        assert PERSISTED.equals(save());
        assert getId() != null;
        assert REMOVED.equals(remove());
    }
    
    private void removeFailTest(final T entity) {
        newInstance();
        initEntity(entity);
        assert getId() == null;
        assert PERSISTED.equals(save());
        assert REMOVED.equals(remove());
        assert !REMOVED.equals(remove());
    }
    
    private void updateSuccessTest(final EntityActionContainer<T> entityActionContainer) {
        newInstance();
        initEntity(entityActionContainer.getEntity());
        assert PERSISTED.equals(save());
        final Object id = getId();
        assert id != null;
        entityActionContainer.execute();
        assert UPDATED.equals(save());
        newInstance();
        setId(id);
        assert !compareEntityValues(entityActionContainer.getEntity());
    }
    
    private void updateFailTest(final EntityActionContainer<T> entityActionContainer) {
        newInstance();
        initEntity(entityActionContainer.getEntity());
        assert PERSISTED.equals(save());
        final Object id = getId();
        assert id != null;
        entityActionContainer.execute();
        assert !UPDATED.equals(save());
        newInstance();
        setId(id);
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
}