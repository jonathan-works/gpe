package br.com.itx.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import javax.persistence.Id;

import org.hibernate.AnnotationException;

public final class AnnotationUtil {

    private AnnotationUtil() {
    }

    /**
     * Verifica se o objeto passado possui a anotação informada.
     * 
     * @param obj Objeto que será verificado
     * @param clazz Anotação que pretende se verificar
     * @return true se o objeto possui a anotação, senão false.
     */
    public static boolean isAnnotationPresent(Object obj,
            Class<? extends Annotation> clazz) {
        return EntityUtil.getEntityClass(obj).isAnnotationPresent(clazz);
    }

    /**
     * Retorna o nome do atributo que possui a anotação informada.
     * 
     * @param object Objeto em que será pesquisada o método que possui a
     *        anotação
     * @param annotationClass @interface da anotação a ser pesquisada.
     * @return Nome do atributo
     */
    public static String getAnnotationField(Object object,
            Class<? extends Annotation> annotationClass) {
        return getAnnotationField(object.getClass(), annotationClass);
    }

    /**
     * Retorna o nome do atributo que possui a anotação informada.
     * 
     * @param classObj Classe em que será pesquisada o método que possui a
     *        anotação
     * @param annotationClass @interface da anotação a ser pesquisada.
     * @return Nome do atributo
     */
    public static String getAnnotationField(Class<? extends Object> classObj, 
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
    public static Object getValue(Object object,
            Class<? extends Annotation> annotationClass) {
        String fieldName = getAnnotationField(object, annotationClass);
        return ComponentUtil.getValue(object, fieldName);
    }

    /**
     * Retorna o valor do Id da entidade.
     * 
     * @param object Objeto em que será pesquisada o método que possui a
     *        anotação
     * @return Valor do Id
     */
    public static Object getIdValue(Object object) {
        return getValue(object, Id.class);
    }
}
