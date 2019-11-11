package br.com.infox.core.abstractenum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractTypeDescriptor;

public class AbstractEnumDescriptor extends AbstractTypeDescriptor<AbstractEnum> {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public AbstractEnumDescriptor(Class type) {
        super(type);
    }

    public AbstractEnum fromString(String string) {
        try {
            Method method = getJavaTypeClass().getMethod("valueOf", String.class);
            return (AbstractEnum) method.invoke(null, string);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new IllegalStateException("Not found method 'valueOf'");
        }
    }

    @Override
    public String toString(AbstractEnum value) {
        return value.name();
    }

    @SuppressWarnings("unchecked")
    public <X> X unwrap(AbstractEnum value, Class<X> type, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (String.class.isAssignableFrom(type)) {
            return (X) value.name();
        }
        throw unknownUnwrap(type);
    }

    public <X> AbstractEnum wrap(X value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (AbstractEnum.class.isInstance(value)) {
            return (AbstractEnum) value;
        }
        if (String.class.isInstance(value)) {
            return fromString((String) value);
        }
        throw unknownWrap(value.getClass());
    }

}