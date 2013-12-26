package br.com.infox.epp.test.crud;

import java.util.List;

import junit.framework.Assert;

import org.jboss.seam.contexts.TestLifecycle;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.mock.JUnitSeamTest;
import org.jboss.seam.servlet.ServletSessionMap;
import org.junit.Test;

public abstract class AbstractGenericCrudTest<T> extends JUnitSeamTest {

    protected final String fillStr(String string, int topLength) {
        if (string == null || string.length() < 1) {
            string = "-";
        }
        
        StringBuilder sb = new StringBuilder(string);
        int length = string.length();
        if (length < topLength) {
            for (int i = 0, l = topLength-length; i < l; i++) {
                sb.append(" ");
            }
        }
        return sb.substring(0, topLength);
    }
    
    /**
     * Call a method binding
     */
    protected final Object invokeMethod(String methodExpression, Object... args) {
        return Expressions.instance().createMethodExpression(methodExpression).invoke(args);
    }

    protected final Object invokeMethod(String componentName, String methodName, Object... args) {
        final String expression = new StringBuilder().append("#{").append(componentName).append(".").append(methodName).append("}").toString();
        return invokeMethod(expression, args);
    }

    /**
     * Evaluate (get) a value binding
     */
    protected final Object getValue(String valueExpression) {
        return Expressions.instance().createValueExpression(valueExpression).getValue();
    }

    protected final Object getValue(String componentName, String fieldName) {
        final String valueExpression = new StringBuilder().append("#{").append(componentName).append(".instance.").append(fieldName).append("}").toString();
        return Expressions.instance().createValueExpression(valueExpression).getValue();
    }

    /**
     * Set a value binding
     */
    protected final void setValue(String valueExpression, Object value) {
        Expressions.instance().createValueExpression(valueExpression).setValue(value);
    }

    protected final void setValue(String componentName, String fieldName, Object value) {
        final String valueExpression = new StringBuilder().append("#{").append(componentName).append(".instance.").append(fieldName).append("}").toString();
        Expressions.instance().createValueExpression(valueExpression).setValue(value);
    }

    protected final void executeTest(Runnable componentTest) {
        TestLifecycle.beginTest(servletContext, new ServletSessionMap(session));
        try {
            componentTest.run();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        } finally {
            TestLifecycle.endTest();
        }
    }

    protected abstract List<T> getInactivateSuccessList();
    protected abstract List<T> getUpdateSuccessList();
    protected abstract List<T> getPersistSuccessList();
    protected abstract List<T> getRemoveSuccessList();
    protected abstract List<T> getInactivateFailList();
    protected abstract List<T> getUpdateFailList();
    protected abstract List<T> getPersistFailList();
    protected abstract List<T> getRemoveFailList();
    
    protected abstract Runnable getInactivateSuccessTest(T entity);
    protected abstract Runnable getUpdateSuccessTest(T entity);
    protected abstract Runnable getRemoveSuccessTest(T entity);
    protected abstract Runnable getPersistSuccessTest(T entity);
    protected abstract Runnable getInactivateFailTest(T entity);
    protected abstract Runnable getUpdateFailTest(T entity);
    protected abstract Runnable getRemoveFailTest(T entity);
    protected abstract Runnable getPersistFailTest(T entity);

    @Test
    public final void initPersistSuccessTest() {
        for (final T entity : getPersistSuccessList()) {
            executeTest(getPersistSuccessTest(entity));
        }
    }

    @Test
    public final void initRemoveSuccessTest() {
        for (final T entity : getRemoveSuccessList()) {
            executeTest(getRemoveSuccessTest(entity));
        }
    }

    @Test
    public final void initUpdateSuccessTest() {
        for (final T entity : getUpdateSuccessList()) {
            executeTest(getUpdateSuccessTest(entity));
        }
    }

    @Test
    public final void initInactivateSuccessTest() {
        for (final T entity : getInactivateSuccessList()) {
            executeTest(getInactivateSuccessTest(entity));
        }
    }

    @Test
    public final void initPersistFailTest() {
        for (final T entity : getPersistFailList()) {
            executeTest(getPersistFailTest(entity));
        }
    }

    @Test
    public final void initRemoveFailTest() {
        for (final T entity : getRemoveFailList()) {
            executeTest(getRemoveFailTest(entity));
        }
    }

    @Test
    public final void initUpdateFailTest() {
        for (final T entity : getUpdateFailList()) {
            executeTest(getUpdateFailTest(entity));
        }
    }

    @Test
    public final void initInactivateFailTest() {
        for (final T entity : getInactivateFailList()) {
            executeTest(getInactivateFailTest(entity));
        }
    }
    
}
