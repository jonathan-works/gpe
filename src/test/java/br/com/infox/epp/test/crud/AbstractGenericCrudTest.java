package br.com.infox.epp.test.crud;

import java.util.List;

import junit.framework.Assert;

import org.jboss.seam.contexts.TestLifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.servlet.ServletSessionMap;
import org.junit.Test;

public abstract class AbstractGenericCrudTest<T> extends JUnitSeamTest {
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
    protected Runnable getPersistSuccessTest(final T entity) {
        return null;
    }
    
    protected List<T> getPersistFailList() {
        return null;
    }
    protected Runnable getPersistFailTest(final T entity) {
        return null;
    }
    
    protected List<T> getInactivateSuccessList() {
        return null;
    }
    protected Runnable getInactivateSuccessTest(final T entity) {
        return null;
    }
    
    protected List<T> getInactivateFailList() {
        return null;
    }
    protected Runnable getInactivateFailTest(final T entity) {
        return null;
    }
    
    protected List<T> getUpdateSuccessList() {
        return null;
    }
    protected Runnable getUpdateSuccessTest(final T entity) {
        return null;
    }
    
    protected List<T> getUpdateFailList() {
        return null;
    }
    protected Runnable getUpdateFailTest(final T entity) {
        return null;
    }
    
    protected List<T> getRemoveSuccessList() {
        return null;
    }
    protected Runnable getRemoveSuccessTest(final T entity) {
        return null;
    }
    
    protected List<T> getRemoveFailList() {
        return null;
    }
    protected Runnable getRemoveFailTest(final T entity) {
        return null;
    }

    @Test
    public final void initPersistSuccessTest() {
        final List<T> list = getPersistSuccessList();
        if (list != null) {
            for (final T entity : list) {
                final Runnable runnableTest = getPersistSuccessTest(entity);
                if (runnableTest == null) {
                    break;
                }
                executeTest(runnableTest);
            }
        }
    }

    @Test
    public final void initRemoveSuccessTest() {
        final List<T> list = getRemoveSuccessList();
        if (list != null) {
            for (final T entity : list) {
                final Runnable runnableTest = getRemoveSuccessTest(entity);
                if (runnableTest == null) {
                    break;
                }
                executeTest(runnableTest);
            }
        }
    }

    @Test
    public final void initUpdateSuccessTest() {
        final List<T> list = getUpdateSuccessList();
        if (list != null) {
            for (final T entity : list) {
                final Runnable runnableTest = getUpdateSuccessTest(entity);
                if (runnableTest == null) {
                    break;
                }
                executeTest(runnableTest);
            }
        }
    }

    @Test
    public final void initInactivateSuccessTest() {
        final List<T> list = getInactivateSuccessList();
        if (list != null) {
            for (final T entity : list) {
                final Runnable runnableTest = getInactivateSuccessTest(entity);
                if (runnableTest == null) {
                    break;
                }
                executeTest(runnableTest);
            }
        }
    }

    @Test
    public final void initPersistFailTest() {
        final List<T> list = getPersistFailList();
        if (list != null) {
            for (final T entity : list) {
                final Runnable runnableTest = getPersistFailTest(entity);
                if (runnableTest == null) {
                    break;
                }
                executeTest(runnableTest);
            }
        }
    }

    @Test
    public final void initRemoveFailTest() {
        final List<T> list = getRemoveFailList();
        if (list != null) {
            for (final T entity : list) {
                final Runnable runnableTest = getRemoveFailTest(entity);
                if (runnableTest == null) {
                    break;
                }
                executeTest(runnableTest);
            }
        }
    }

    @Test
    public final void initUpdateFailTest() {
        final List<T> list = getUpdateFailList();
        if (list != null) {
            for (final T entity : list) {
                final Runnable runnableTest = getUpdateFailTest(entity);
                if (runnableTest == null) {
                    break;
                }
                executeTest(runnableTest);
            }
        }
    }

    @Test
    public final void initInactivateFailTest() {
        final List<T> list = getInactivateFailList();
        if (list != null) {
            for (final T entity : list) {
                final Runnable runnableTest = getInactivateFailTest(entity);
                if (runnableTest == null) {
                    break;
                }
                executeTest(runnableTest);
            }
        }
    }

}
