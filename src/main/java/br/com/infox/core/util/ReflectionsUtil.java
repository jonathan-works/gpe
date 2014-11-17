package br.com.infox.core.util;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public final class ReflectionsUtil {

    private static final LogProvider LOG = Logging.getLogProvider(ReflectionsUtil.class);

    private ReflectionsUtil() {
    }

    private static Field getField(Object o, String fieldName) {
        Exception exc = null;
        Class<?> cl = o.getClass();
        while (cl != null) {
            try {
                Field f = cl.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (Exception e) {
                LOG.warn(".getField(o, fieldName)", e);
                cl = cl.getSuperclass();
                exc = e;
            }
        }
        LOG.trace(exc);
        return null;
    }

    public static Object getValue(Object o, String fieldName) {
        try {
            Field field = getField(o, fieldName);
            if (field != null) {
                return field.get(o);
            }
        } catch (Exception e) {
            LOG.error(".getValue()", e);
        }
        return null;
    }

    public static String getStringValue(Object o, String fieldName) {
        return (String) getValue(o, fieldName);
    }

    public static void setValue(Object o, String fieldName, Object value) {
        try {
            Field field = getField(o, fieldName);
            if (field != null) {
                field.set(o, value);
            }
        } catch (Exception e) {
            LOG.error(".setValue()", e);
        }
    }

    public static boolean hasAnnotation(PropertyDescriptor pd,
            Class<? extends Annotation> annotation) {
        Method readMethod = pd.getReadMethod();
        if (readMethod != null) {
            if (readMethod.isAnnotationPresent(annotation)) {
                return true;
            }

            Class<?> declaringClass = readMethod.getDeclaringClass();
            try {
                Field field = declaringClass.getDeclaredField(pd.getName());
                return field.isAnnotationPresent(annotation);
            } catch (NoSuchFieldException ex) {
                LOG.debug("hasAnnotation(pd, annotation)", ex);
                return false;
            }

        }
        return false;
    }
    
    public static Object newInstance(Class<?> clazz, Class<?> parameterType, Object value) {
    	return newInstance(clazz, new Class<?>[]{parameterType} , new Object[] {value});
    }
    
    public static Object newInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] values) {
    	Object ret = null;
		try {
			Constructor<?> constructor = clazz.getConstructor(parameterTypes);
			ret = constructor.newInstance(values);
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOG.debug(".newInstance", e);
		}
    	return ret;
    }

}
