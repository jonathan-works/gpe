package br.com.infox.core.util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Named;

import org.apache.commons.beanutils.BeanUtilsBean;

import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

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

    @SuppressWarnings("unchecked")
	public static <E> E getValue(Object o, String fieldName) {
        try {
            Field field = getField(o, fieldName);
            if (field != null) {
                return (E) field.get(o);
            }
        } catch (Exception e) {
            LOG.error(".getValue()", e);
        }
        return null;
    }

    public static String getStringValue(Object o, String fieldName) {
        return getValue(o, fieldName);
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
    
    public static void copyProperties(Object objectDest, Object objectOrig) {
		try {
			BeanUtilsBean.getInstance().copyProperties(objectDest, objectOrig);
		} catch (IllegalAccessException | InvocationTargetException e) {
			LOG.error("copyProperties", e);
		}
	}
    
    public static Object newInstance(Class<?> clazz, Class<?> parameterType, Object value) {
    	return newInstance(clazz, new Class<?>[]{parameterType} , new Object[] {value});
    }
    
    public static Object newInstance(Class<?> clazz, Class<?>[] parameterTypes, Object[] values) {
    	Object ret = null;
		try {
			if (clazz.isEnum()) {
				return newInstanceEnum(clazz, values[0]);
			} else {
				return newInstanceClass(clazz, parameterTypes, values);
			}
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOG.debug(".newInstance", e);
		}
    	return ret;
    }
    
    private static Object newInstanceEnum(Class<?> clazz, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    	Method method = clazz.getMethod("valueOf", String.class);
		return method.invoke(null, value);
    }
    
    private static Object newInstanceClass(Class<?> clazz, Class<?>[] parameterTypes, Object[] values) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, InstantiationException {
    	Constructor<?> constructor = clazz.getConstructor(parameterTypes);
		return constructor.newInstance(values);
    }
    
    public static String getCdiComponentName(Class<?> clazz) {
    	while ( clazz!=null && !Object.class.equals(clazz) ) {
           Named named = clazz.getAnnotation(Named.class);
           if ( named != null ) {
        	   if (named.value().isEmpty()) {
        		   return Introspector.decapitalize(clazz.getSimpleName());
        	   } else {
        		   return named.value();
        	   }
           }
           clazz = clazz.getSuperclass();
        }
        return null;
    }

}
