/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informação Ltda.

 Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; versão 2 da Licença.
 Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 
 Consulte a GNU GPL para mais detalhes.
 Você deve ter recebido uma cópia da GNU GPL junto com este programa; se não, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.util;

import static br.com.infox.core.constants.WarningConstants.UNCHECKED;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;

public final class EntityUtil implements Serializable {
		
	public static final String ENTITY_MANAGER_NAME = "entityManager";
	private static final long serialVersionUID = 1L;
	private static final LogProvider LOG = Logging.getLogProvider(EntityUtil.class);

	private EntityUtil() { }
	
	/**
	 * Metodo que recebe uma entidade e devolve o PropertyDescriptor do campo id
	 * procurando pela anotação @id
	 * @param objId Entidade
	 * @return
	 */
	public static PropertyDescriptor getId(Object objId) {
		if (!EntityUtil.isEntity(objId)) {
			throw new IllegalArgumentException("O objeto não é uma entidade: " + 
					objId.getClass().getName());
		}				
		Class<?> cl = objId.getClass();
		if (cl.getName().indexOf("javassist") > -1) {
			cl = cl.getSuperclass();
		}
		
		return getId(cl);
	}
	
	/**
	 * Metodo que recebe um Class e devolve o PropertyDescriptor do campo id
	 * procurando pela anotações @id e @EmbeddedId
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
	 * @param obj
	 * @return
	 */
	public static boolean isEntity(Object obj) {
		Class<?> cl = getEntityClass(obj);
		return isEntity(cl);
	}
	
	/**
	 * Testa se a classe possui a anotação @Entity
	 * @param obj
	 * @return
	 */
	public static boolean isEntity(Class<?> cl) {
		if (cl.isPrimitive() || String.class.getPackage().equals(cl.getPackage())) {
			return false;
		} else {
			return cl.isAnnotationPresent(Entity.class);
		}
	}	
	
	public static boolean isAnnotationPresent(Object obj, Class<? extends Annotation> clazz) {
		Class<?> cl = getEntityClass(obj);
		return cl.isAnnotationPresent(clazz);
	}	
	
	/**
	 * Metodo que recebe um objeto de uma entidade e pega por reflexão o objeto com o id 
	 * desta entidade.
	 * @param entidade
	 * @return
	 */
	public static Object getEntityIdObject(Object entidade) {
		if (!EntityUtil.isEntity(entidade)) {
			throw new IllegalArgumentException("O objeto não é uma entidade: " + 
					entidade.getClass().getName());
		}		
		Class<? extends Object> cl = entidade.getClass();
		if (!cl.isPrimitive() && !cl.getPackage().equals(String.class.getPackage())) {
			PropertyDescriptor id = getId(entidade);
			if (id != null) {
				Method readMethod = id.getReadMethod();
				try {
					return readMethod.invoke(entidade, new Object[0]);
				} catch (Exception e) {
				    LOG.error(".getEntityIdObject()", e);
				} 
			} else {
				LOG.error("Não foi encontrado um PropertyDescriptor para o " +
						"Id da entidade " + entidade.getClass().getName());
			}
		} 	
		return null;
	}
	
	/**
	 * Metodo que devolve a classe da entidade. Caso a entidade seja um proxy (javassist),
	 * retorna a classe pai usando o {@link java.lang.Class#getSuperclass() getSuperclass}
	 * @param entity
	 * @return
	 */
	public static Class<?> getEntityClass(Object entity) {
		Class<?> cl = entity.getClass();
		if (cl.getName().indexOf("javassist") > -1) {
			cl = cl.getSuperclass();
		}
		return cl;
	}
	
	/**
	 * Metodo que recebe uma entidade e cria um objeto do mesmo tipo e copia os 
	 * atributos para esta nova entidade.
	 * @param <E>
	 * @param origem
	 * @param copyLists
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings(UNCHECKED)
	public static <E> E cloneEntity(E origem, boolean copyLists) throws 
				InstantiationException, IllegalAccessException {
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
	
	public static Object cloneObject(Object origem, boolean copyLists) throws 
				InstantiationException, IllegalAccessException {
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
		return pd != null && (ComponentUtil.hasAnnotation(pd, Id.class) || 
				ComponentUtil.hasAnnotation(pd,EmbeddedId.class));
	}
	
	private static boolean isAceptable(PropertyDescriptor pd) {
		return pd != null && !ComponentUtil.hasAnnotation(pd,Transient.class) &&
					(ComponentUtil.hasAnnotation(pd,Column.class) 
				|| ComponentUtil.hasAnnotation(pd,ManyToOne.class));
	}
	
	private static boolean isRelacao(PropertyDescriptor pd) {
		return pd != null && (ComponentUtil.hasAnnotation(pd,ManyToMany.class) 
				|| ComponentUtil.hasAnnotation(pd,OneToMany.class));
	}	
	
	public static EntityManager getEntityManager(){
		return ComponentUtil.getComponent(ENTITY_MANAGER_NAME);
	}
	
	public static Query createQuery(String hql) {
		return getEntityManager().createQuery(hql);
	}

	public static void flush(){
		getEntityManager().flush();
	} 

	/**
	 * Devolve um List com todos os elementos de uma determinada entidade.
	 * Ex: <code>List{@literal <E>} resultList = EntityUtil.getEntityList(Parametro.class)<code>;
	 * @param <E> O type da Entidade
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings(UNCHECKED)
	public static <E> List<E> getEntityList(Class<E> clazz) {
		StringBuilder sb = new StringBuilder();
		sb.append("select o from ").append(clazz.getName()).append(" o");
		return getEntityManager().createQuery(sb.toString()).getResultList();
	}
	
	@SuppressWarnings(UNCHECKED)
	public static <E> Class<E> getParameterizedTypeClass(Class<E> clazz) {
		Class<E> entityClass;
		java.lang.reflect.Type type = clazz.getGenericSuperclass();
        if (type instanceof java.lang.reflect.ParameterizedType) {
        	java.lang.reflect.ParameterizedType paramType = 
        							(java.lang.reflect.ParameterizedType) type;
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
	
}