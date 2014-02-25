package br.com.itx.util;

import java.lang.reflect.Field;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

public final class ReflectionsUtil {

    private static final LogProvider LOG = Logging.getLogProvider(ReflectionsUtil.class);

    private ReflectionsUtil() {
    }

    public static Field getField(Object o, String fieldName) {
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

}
