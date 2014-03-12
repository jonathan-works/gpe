package br.com.infox.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.hibernate.AnnotationException;

import br.com.infox.seam.util.ComponentUtil;

public final class AnnotationUtil {

    private AnnotationUtil() {
    }

    /**
     * Retorna o nome do atributo que possui a anotação informada.
     * 
     * @param classObj Classe em que será pesquisada o método que possui a
     *        anotação
     * @param annotationClass @interface da anotação a ser pesquisada.
     * @return Nome do atributo
     */
    private static String getAnnotationField(Class<? extends Object> classObj,
            Class<? extends Annotation> annotationClass) {
        for (Method m : classObj.getMethods()) {
            if (!m.isAnnotationPresent(annotationClass)) {
                continue;
            }

            String fieldName = m.getName();
            fieldName = fieldName.startsWith("is") ? fieldName.substring(2) : fieldName.substring(3);
            return Character.toLowerCase(fieldName.charAt(0))
                    + fieldName.substring(1);
        }

        for (Field f : classObj.getDeclaredFields()) {
            if (f.isAnnotationPresent(annotationClass)) {
                return f.getName();
            }
        }

        String msg = MessageFormat.format("Missing annotation @{0}", annotationClass.getSimpleName());
        throw new AnnotationException(msg);
    }

    /**
     * Retorna o valor do atributo que possui a anotação informada.
     * 
     * @param object Objeto em que será pesquisada o método que possui a
     *        anotação
     * @param annotationClass anotação a ser pesquisada nos métodos do objeto
     * @return Valor do atributo
     */
    public static Object getAnnotatedAttributeValue(Object object,
            Class<? extends Annotation> annotationClass) {
        String fieldName = getAnnotationField(object.getClass(), annotationClass);
        return ComponentUtil.getValue(object, fieldName);
    }

}
