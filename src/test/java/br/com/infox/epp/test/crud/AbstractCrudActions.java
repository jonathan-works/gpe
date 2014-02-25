package br.com.infox.epp.test.crud;

import static br.com.infox.constants.WarningConstants.UNCHECKED;
import static java.text.MessageFormat.format;

import java.util.ArrayList;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.ValueExpression;

public abstract class AbstractCrudActions<E> implements CrudActions<E> {
    private static final String INACTIVATE = "inactive";
    private static final String ID = "instanceId";
    private static final String REMOVE = "remove";
    private static final String SAVE = "save";
    private static final String INSTANCE = "instance";
    private static final String NEW_INSTANCE = "newInstance";

    private static final String COMP_EXP = "'#{'{0}.{1}'}'";

    private static final String ENT_EXP = "'#{'{0}.instance.{1}'}'";

    private final String componentName;

    public AbstractCrudActions(final String componentName) {
        this.componentName = componentName;
    }

    private ValueExpression<Object> createValueExpression(
            final String valueExpression) {
        return Expressions.instance().createValueExpression(valueExpression);
    }

    @Override
    public final E createInstance() {
        this.newInstance();
        return this.getInstance();
    }

    @SuppressWarnings(UNCHECKED)
    public final <R> R getComponentValue(final String field) {
        final String valueExpression = format(COMP_EXP, this.componentName, field);
        return (R) createValueExpression(valueExpression).getValue();
    }

    @SuppressWarnings(UNCHECKED)
    public final <R> R getEntityValue(final String field) {
        final String valueExpression = format(ENT_EXP, this.componentName, field);
        return (R) createValueExpression(valueExpression).getValue();
    }
    
    public final Integer getId() {
        return getComponentValue(ID);
    }
    
    @SuppressWarnings(UNCHECKED)
    public final E getInstance() {
        return (E) getComponentValue(INSTANCE);
    }
    
    public final String inactivate() {
        final Class<?>[] paramTypes = {Object.class};
        return this.invokeMethod(INACTIVATE, String.class, paramTypes, getInstance());
    }

    public final Object invokeMethod(final String methodName) {
        return this.invokeMethod(componentName, methodName, Object.class, new Class<?>[]{}, new Object[]{});
    }

    public final <R> R invokeMethod(final String methodName, final Class<R> returnType, final Class<?>[] paramTypes, final Object...args) {
        return this.invokeMethod(this.componentName, methodName, returnType, paramTypes, args);
    }

    public final <R> R invokeMethod(final String methodName,
            final Class<R> returnType, final Object... args) {
        return this.invokeMethod(this.componentName, methodName, returnType, args);
    }

    @Override
    public final Object invokeMethod(final String componentName, final String methodName) {
        return this.invokeMethod(componentName, methodName, Object.class, new Class<?>[]{}, new Object[]{});
    }
    
    @Override
    public final <R> R invokeMethod(final String componentName, final String methodName,
            final Class<R> returnType, final Class<?>[] paramTypes, final Object... args) {
        final Expressions expressionFactory = Expressions.instance();
        final String expressionString = format(COMP_EXP, componentName, methodName);
        return expressionFactory.createMethodExpression(expressionString, returnType, paramTypes).invoke(args);
    }
    
    @Override
    public final <R> R invokeMethod(final String componentName, final String methodName,
            final Class<R> returnType, final Object... args) {
        final ArrayList<Class<?>> classList = new ArrayList<>();
        for (final Object object : args) {
            classList.add(object.getClass());
        }
        final Class<?>[] types = classList.toArray(new Class<?>[classList.size()]);
        return this.invokeMethod(componentName, methodName, returnType, types, args);
    }

    public final void newInstance() {
        this.invokeMethod(NEW_INSTANCE);
    }

    public final String remove() {
        return this.invokeMethod(REMOVE, String.class);
    }

    public final String remove(final E entity) {
        final Class<?>[] paramTypes = {Object.class};
        return this.invokeMethod(REMOVE, String.class, paramTypes, entity);
    }

    public final E resetInstance(final Object id) {
        invokeMethod("onClickSearchTab");
        this.setId(id);
        return this.getInstance();
    }

    public final String save() {
        return this.invokeMethod(SAVE, String.class);
    }

    public final void setComponentValue(final String field,
            final Object value) {
        final String valueExpression = format(COMP_EXP, this.componentName, field);
        createValueExpression(valueExpression).setValue(value);
    }

    public final void setEntityValue(final String field, final Object value) {
        final String valueExpression = format(ENT_EXP, this.componentName, field);
        createValueExpression(valueExpression).setValue(value);
    }

    public final void setId(final Object value) {
        setComponentValue(ID, value);
    }

    public final void setInstance(final E value) {
        setComponentValue(INSTANCE, value);
    }
}