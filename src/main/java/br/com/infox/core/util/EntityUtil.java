package br.com.infox.core.util;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Transient;

import org.jboss.seam.util.Reflections;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;
import br.com.infox.seam.util.ComponentUtil;

public final class EntityUtil implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final LogProvider LOG = Logging.getLogProvider(EntityUtil.class);

    private EntityUtil() {
    }

    /**
     * Metodo que recebe uma entidade e devolve o PropertyDescriptor do campo id
     * procurando pela anotação @id
     * 
     * @param objId Entidade
     * @return
     */
    public static PropertyDescriptor getId(Object objId) {
        if (!EntityUtil.isEntity(objId)) {
            throw new IllegalArgumentException("O objeto não é uma entidade: "
                    + objId.getClass().getName());
        }
        Class<?> cl = objId.getClass();
        if (cl.getName().indexOf("javassist") > -1) {
            cl = cl.getSuperclass();
        }
        if(cl.getName().contains("_$$_")){
        	cl = cl.getSuperclass();
        }
        return getId(cl);
    }

    /**
     * Retorna o id de uma entidade
     * 
     * @param objId a entidade da qual se deseja descobri o id
     * 
     * @throws IllegalArgumentException quando o objeto não é uma entidade
     * */
    @Deprecated
    public static Object getIdValue(Object objId) throws IllegalAccessException, InvocationTargetException {
        if (!EntityUtil.isEntity(objId)) {
            throw new IllegalArgumentException("O objeto não é uma entidade: "
                    + objId.getClass().getName());
        }
        Class<?> cl = objId.getClass();
        if (cl.getName().indexOf("javassist") > -1) {
            cl = cl.getSuperclass();
        }
        if(cl.getName().contains("_$$_")){
        	cl = cl.getSuperclass();
        }
        return getId(cl).getReadMethod().invoke(objId);
    }

    /**
     * Metodo que recebe um Class e devolve o PropertyDescriptor do campo id
     * procurando pela anotações @id e @EmbeddedId
     * 
     * @param objId Entidade
     * @return
     */
    public static PropertyDescriptor getId(Class<?> clazz) {
        PropertyDescriptor[] pds = ComponentUtil.getPropertyDescriptors(clazz);
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            if (isId(pd)) {
                return pd;
            }
        }
        return null;
    }

    /**
     * Testa se o objeto possui a anotação @Entity
     * 
     * @param obj
     * @return
     */
    public static boolean isEntity(Object obj) {
        Class<?> cl = getEntityClass(obj);
        return isEntity(cl);
    }

    /**
     * Testa se a classe possui a anotação @Entity
     * 
     * @param obj
     * @return
     */
    public static boolean isEntity(Class<?> cl) {
        if (cl.isPrimitive()
                || String.class.getPackage().equals(cl.getPackage())) {
            return false;
        } else {
            return cl.isAnnotationPresent(Entity.class);
        }
    }

    public static boolean isAnnotationPresent(Object obj,
            Class<? extends Annotation> clazz) {
        Class<?> cl = getEntityClass(obj);
        return cl.isAnnotationPresent(clazz);
    }

    /**
     * Metodo que recebe um objeto de uma entidade e pega por reflexão o objeto
     * com o id desta entidade.
     * 
     * @param entidade
     * @return
     */
    public static Object getEntityIdObject(Object entidade) {
        if (!EntityUtil.isEntity(entidade)) {
            throw new IllegalArgumentException("O objeto não é uma entidade: "
                    + entidade.getClass().getName());
        }
        Class<? extends Object> cl = entidade.getClass();
        if (!cl.isPrimitive()
                && !cl.getPackage().equals(String.class.getPackage())) {
            PropertyDescriptor id = getId(entidade);
            if (id != null) {
                Method readMethod = id.getReadMethod();
                try {
                    return readMethod.invoke(entidade, new Object[0]);
                } catch (Exception e) {
                    LOG.error(".getEntityIdObject()", e);
                }
            } else {
                LOG.error("Não foi encontrado um PropertyDescriptor para o "
                        + "Id da entidade " + entidade.getClass().getName());
            }
        }
        return null;
    }

    /**
     * Método utilizado como alternativa mais recente para {@link #getEntityClass(Object)}
     * @param entity
     * @return
     */
    public static Class<?> getClass(Object entity) {
    	return HibernateUtil.getClass(entity);
    }
    
    /**
     * Metodo que devolve a classe da entidade. Caso a entidade seja um proxy
     * (javassist), retorna a classe pai usando o
     * {@link java.lang.Class#getSuperclass() getSuperclass}
     * Método substituído por {@link #getClass(Object)}
     * 
     * @param entity
     * @return
     */
    @Deprecated
    public static Class<?> getEntityClass(Object entity) {
        Class<?> cl = entity.getClass();
        if (cl.getName().indexOf("javassist") > -1) {
            cl = cl.getSuperclass();
        }
        if(cl.getName().contains("_$$_")){
        	cl = cl.getSuperclass();
        }
        return cl;
    }

    /**
     * Metodo que recebe uma entidade e cria um objeto do mesmo tipo e copia os
     * atributos para esta nova entidade.
     * 
     * @param <E>
     * @param origem
     * @param copyLists
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @SuppressWarnings(UNCHECKED)
    public static <E> E cloneEntity(E origem, boolean copyLists) throws InstantiationException, IllegalAccessException {
        Class<?> cl = getEntityClass(origem);
        E destino = (E) cl.newInstance();
        PropertyDescriptor[] pds = ComponentUtil.getPropertyDescriptors(cl);
        for (PropertyDescriptor pd : pds) {
            if ((!isId(pd) && isAceptable(pd)) || (isRelacao(pd) && copyLists)) {
                Method rm = pd.getReadMethod();
                Method wm = pd.getWriteMethod();
                if (wm != null) {
                    Object value = Reflections.invokeAndWrap(rm, origem, new Object[0]);
                    Reflections.invokeAndWrap(wm, destino, value);
                }
            }
        }
        return destino;
    }

    public static Object cloneObject(Object origem, boolean copyLists) throws InstantiationException, IllegalAccessException {
        Class<?> cl = getEntityClass(origem);
        Object destino = cl.newInstance();
        PropertyDescriptor[] pds = ComponentUtil.getPropertyDescriptors(cl);
        for (PropertyDescriptor pd : pds) {
            if ((!isId(pd) && isAceptable(pd)) || (isRelacao(pd) && copyLists)) {
                Method rm = pd.getReadMethod();
                Method wm = pd.getWriteMethod();
                if (wm != null) {
                    Object value = Reflections.invokeAndWrap(rm, origem, new Object[0]);
                    Reflections.invokeAndWrap(wm, destino, value);
                }
            }
        }
        return destino;
    }

    private static boolean isId(PropertyDescriptor pd) {
        return pd != null
                && (ReflectionsUtil.hasAnnotation(pd, Id.class) || ReflectionsUtil.hasAnnotation(pd, EmbeddedId.class));
    }

    private static boolean isAceptable(PropertyDescriptor pd) {
        return pd != null
                && !ReflectionsUtil.hasAnnotation(pd, Transient.class)
                && (ReflectionsUtil.hasAnnotation(pd, Column.class) || ReflectionsUtil.hasAnnotation(pd, ManyToOne.class)
                		|| ReflectionsUtil.hasAnnotation(pd, JoinColumn.class));
    }

    private static boolean isRelacao(PropertyDescriptor pd) {
        return pd != null
                && (ReflectionsUtil.hasAnnotation(pd, ManyToMany.class) || ReflectionsUtil.hasAnnotation(pd, OneToMany.class));
    }

    @SuppressWarnings(UNCHECKED)
    public static <E> Class<E> getParameterizedTypeClass(Class<E> clazz) {
        Class<E> entityClass;
        java.lang.reflect.Type type = clazz.getGenericSuperclass();
        if (clazz.getCanonicalName().contains("_$$_")) { // Proxy
            type = ((Class<?>) type).getGenericSuperclass();
        }
        if (type instanceof java.lang.reflect.ParameterizedType) {
            java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) type;
            entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException("Não foi possivel pegar a Entidade por reflexão");
        }
        return entityClass;
    }

    public static <E> E newInstance(Class<E> clazz) {
        try {
            return getParameterizedTypeClass(clazz).newInstance();
        } catch (Exception e) {
            LOG.error(".newInstance()", e);
        }
        return null;
    }
    
    public static Object getIdentifier(Object entity) {
        PersistenceUnitUtil puu = EntityManagerProducer.getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil();
        return puu.getIdentifier(entity);
    }

}
