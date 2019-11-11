package br.com.infox.core.abstractenum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;

import br.com.infox.core.type.Displayable;

public abstract class AbstractEnum implements Displayable {

    public abstract String name();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected static void initSubtypes(Class clazz) {
        Reflections reflections = new Reflections("br.com.infox");
        Set<Class<?>> subTypesOf = reflections.getSubTypesOf(clazz);
        for (Class class1 : subTypesOf) {
            Method[] declaredMethods = class1.getDeclaredMethods();
            for (Method method : declaredMethods) {
                boolean annotationPresent = method.isAnnotationPresent(PostConstruct.class);
                if (annotationPresent) {
                    method.setAccessible(true);
                    try {
                        method.invoke(null);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                        throw new IllegalStateException("Error trying to execute method annotated with 'PostConstruct'");
                    }
                }
            }
        }
    }
}
