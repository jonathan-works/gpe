/*
 IBPM - Ferramenta de produtividade Java
 Copyright (c) 1986-2009 Infox Tecnologia da Informa��o Ltda.

 Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 sob os termos da GNU GENERAL PUBLIC LICENSE (GPL) conforme publicada pela 
 Free Software Foundation; vers�o 2 da Licen�a.
 Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 
 Consulte a GNU GPL para mais detalhes.
 Voc� deve ter recebido uma c�pia da GNU GPL junto com este programa; se n�o, 
 veja em http://www.gnu.org/licenses/   
*/
package br.com.itx.util;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.naming.NamingException;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Query;
import javax.persistence.Transient;

import org.hibernate.AssertionFailure;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Reflections;

public final class EntityUtil implements Serializable {
		
	public static final String ENTITY_MANAGER_NAME = "entityManager";
	private static final long serialVersionUID = 1L;
	private static final LogProvider log = Logging.getLogProvider(EntityUtil.class);

	private EntityUtil() { }
	
	/**
	 * Metodo que recebe um objeto, que representa um Id composto de uma entidade,
	 * e retorna uma String com os valores dos fields do id separados pelo char '-'
	 * @param O objeto que representa um id composto
	 * @return
	 */
	public static String getCompositeId(Object objId) {
		StringBuilder sb = new StringBuilder("");
		if (objId != null) {
			PropertyDescriptor[] pds = ComponentUtil.getPropertyDescriptors(objId);
			for (int i = 0; i < pds.length; i++) {
				try {
					PropertyDescriptor pd = pds[i];
					if (pd.getName().equals("class")) {
						continue;
					}
					if (sb.length() > 0) {
						sb.append('-');
					}
					sb.append(getProperty(objId, pd));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
	
	private static String getProperty(Object objId, PropertyDescriptor pd) 
	throws Exception {
		Class<?> cl = pd.getPropertyType();
		Object value = null;
		Method m = pd.getReadMethod();
		if (m != null) {
			value = m.invoke(objId);
		}
		if (value != null) {
			if (cl.isAnnotationPresent(Entity.class)) {
				PropertyDescriptor pd2 = getId(value);
				return getProperty(value, pd2);
			} else {
				return value.toString();
			}
		}	
		return "";
	}
	
	public static void setCompositeId(Object objId, String id) {
		if (id != null && !id.equals("")) {
			PropertyDescriptor[] pds = 
				ComponentUtil.getPropertyDescriptors(objId);
			int cnt = 0;
			String[] piece = id.split("-");
			for (int i = 0; i < pds.length; i++) {
				try { 
					PropertyDescriptor pd = pds[i];
					if (pd.getName().equals("class")) {
						continue;
					}
					if (cnt < piece.length) {
						String value = piece[cnt];
						if (!value.trim().equals("")) {
							setProperty(objId, pd, value);
						}
					}	
					cnt++;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}	
	}
	
	private static void setProperty(Object objId, 
			PropertyDescriptor pd, String strValue) throws Exception {
		Class<?> cl = pd.getPropertyType();
		Object value = null;
		if (cl.isAnnotationPresent(Entity.class)) {
			value = cl.newInstance();
			PropertyDescriptor pd2 = getId(value);
			setProperty(value, pd2, strValue);
		} else {
			value = cl.getConstructor(String.class).newInstance(strValue);
		}
		Method m = pd.getWriteMethod();
		if (m != null) {
			m.invoke(objId, value);
		}
	}
	
	/**
	 * Metodo que recebe uma entidade e devolve o PropertyDescriptor do campo id
	 * procurando pela anota��o @id
	 * @param objId Entidade
	 * @return
	 */
	public static PropertyDescriptor getId(Object objId) {
		if (!EntityUtil.isEntity(objId)) {
			throw new IllegalArgumentException("O objeto n�o � uma entidade: " + 
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
	 * procurando pela anota��es @id e @EmbeddedId
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
	 * Testa de o objeto possui a anota��o @Entity
	 * @param obj
	 * @return
	 */
	public static boolean isEntity(Object obj) {
		Class<?> cl = getEntityClass(obj);
		return isEntity(cl);
	}
	
	/**
	 * Testa de a classe possui a anota��o @Entity
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
	 * Metodo que recebe um objeto de uma entidade e pega por reflex�o o objeto com o id 
	 * desta entidade.
	 * @param entidade
	 * @return
	 */
	public static Object getEntityIdObject(Object entidade) {
		if (!EntityUtil.isEntity(entidade)) {
			throw new IllegalArgumentException("O objeto n�o � uma entidade: " + 
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
					e.printStackTrace();
				} 
			} else {
				log.error("N�o foi encontrado um PropertyDescriptor para o " +
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
	@SuppressWarnings("unchecked")
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
	
	public static <E> List<E> cloneListEntity(List<E> origem, boolean copyLists) throws 
				InstantiationException, IllegalAccessException {
		List<E> destino = new ArrayList<E>();
		for (E entity: origem) {
			destino.add(cloneEntity(entity, copyLists));
		}
		return destino;
	}
	
	//TODO metodo de teste pois o acina estava dando erro em: oldEntity = (T) EntityUtil.cloneEntity(instance, false);
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
	
	/**
	 * Metodo que recebe uma entidade e seta null no atributo que corresponde ao id. Caso
	 * o tipo deste campo seja primitivo coloca o numero 0.
	 * Isto � utilizado porque o hibernate aloca um Id para a entidade antecipadamente e
	 * com isso caso ocorra um erro, como de viola��o de contraint, a entidade fica com um
	 * id inv�lido e ocorre um erro ao persiti essa entidade. 
	 * @param entidade
	 */
	public static void setNullOnEntityId(Object entidade) throws Exception {
		PropertyDescriptor pd = EntityUtil.getId(entidade);
		Method writeMethod = pd.getWriteMethod();
		Class<?> propertyType = pd.getPropertyType();
		writeMethod.invoke(entidade, propertyType.isPrimitive() ? 0 : new Object[1]);
	}
	
	/**
	 * Metodo que cria um novo ArrayList para os atributos List de relacionamento 
	 * da entidade. Esto � feito pois em caso de um erro na persistencia, os
	 * List ficam com referencia para a Entidade que deveria ter sido persistida 
	 * (O hibernate gera um id pra estidade antes de inserir e em uma exece��o, os
	 * list (PersistentBags) apontam para este id que n�o existe.
	 * @param entidade
	 * @throws Exception
	 */
	public static void clearEntityLists(Object entidade) throws Exception {
		List<PropertyDescriptor> descriptors = getPropertyDescriptors(entidade, OneToMany.class);
		for (PropertyDescriptor pd : descriptors) {
			Class<?> type = pd.getPropertyType();
			type.getGenericSuperclass();
			if (type.equals(List.class)) {
				pd.getWriteMethod().invoke(entidade, new ArrayList<Object>(0));
			}
		}
	}

	
	/**
	 * Metodo que devolve todos os PropertyDescriptor de uma entidade
	 * que contenham determinada Annotation. O metodo faz um teste se a classe
	 * foi criada por proxy, caso sim pega a classe pai, para buscar pelas 
	 * anota��es
	 * @param entidade
	 * @param annotationClass
	 * @return
	 */
	public static List<PropertyDescriptor> getPropertyDescriptors(Object entidade, 
			Class<? extends Annotation> annotationClass) {
		List<PropertyDescriptor> descriptors = new ArrayList<PropertyDescriptor>();
		Class<?> cl = getEntityClass(entidade);
		PropertyDescriptor[] pds = ComponentUtil.getPropertyDescriptors(cl);
		for (PropertyDescriptor pd : pds) {
			if (ComponentUtil.hasAnnotation(pd, annotationClass)) {
				descriptors.add(pd);
			}
		}
		return descriptors;
	}		

	/**
	 * Retorna o primeiro objeto do resultado da query
	 * 
	 * @param query
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getSingleResult(Query query) {
		query.setMaxResults(1);
		List<?> list = query.getResultList();
		if (list == null || list.size() == 0) {
			return null;
		}
		return (T) list.get(0);
	}
	
	public static EntityManager getEntityManager(){
		return ComponentUtil.getComponent(ENTITY_MANAGER_NAME);
	}
	
	public static Query createQuery(String hql) {
		return getEntityManager().createQuery(hql);
	}

	public static void flush(){
		try { 
			getEntityManager().flush();
		} catch (AssertionFailure e) { /* bug hibernate */ }
	} 

	public static void flush(EntityManager em){
		try { 
			em.flush();
		} catch (AssertionFailure e) { /* bug hibernate */ }
	}
		
	/**
	 * Devolve um List com todos os elementos de uma determinada entidade.
	 * Ex: <code>List{@literal <E>} resultList = EntityUtil.getEntityList(Parametro.class)<code>;
	 * @param <E> O type da Entidade
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <E> List<E> getEntityList(Class<E> clazz) {
		StringBuilder sb = new StringBuilder();
		sb.append("select o from ").append(clazz.getName()).append(" o");
		return getEntityManager().createQuery(sb.toString()).getResultList();
	}
	
	/**
	 * Atalho para busca de entidades pelo id
	 * 
	 * @param <E>
	 * @param clazz classe da entidade a ser pesquisada
	 * @param id
	 * @return
	 */
	public static <E> E find(Class<E> clazz, Object id) {
		if(id == null) {
			return null;
		}
		return getEntityManager().find(clazz, id);
	}

	/**
	 * Retorna o entityManager do JPA para quando n�o for poss�vel acessar o 
	 * do Seam.
	 * @param persistenceUnitJndiName Nome do Unit que ser� criado o entityManager pelo Factory
	 * @return EntityManager
	 */
	public static EntityManager createEntityManagerFactory(String persistenceUnitJndiName) {
		try {
			EntityManagerFactory emf = (EntityManagerFactory) Naming.getInitialContext().lookup(persistenceUnitJndiName);
			return emf.createEntityManager();
		} catch (NamingException e) {
            throw new IllegalArgumentException("EntityManagerFactory not found in JNDI : " + persistenceUnitJndiName, e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <E> Class<E> getParameterizedTypeClass(Class<E> clazz) {
		Class<E> entityClass;
		java.lang.reflect.Type type = clazz.getGenericSuperclass();
        if (type instanceof java.lang.reflect.ParameterizedType) {
        	java.lang.reflect.ParameterizedType paramType = 
        							(java.lang.reflect.ParameterizedType) type;
            entityClass = (Class<E>) paramType.getActualTypeArguments()[0];
        } else {
            throw new IllegalArgumentException("N�o foi possivel pegar a Entidade por reflex�o");
        }
        return entityClass;
    }
	
	public static <E> E newInstance(Class<E> clazz) {
		try {
			return getParameterizedTypeClass(clazz).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<Object> getIdsFromList(List<?> listaObj) {
		List<Object> list = new ArrayList<Object>();
		if (listaObj == null) {
			return Collections.emptyList();
		}
		for (Object object : listaObj) {
			Object entityIdObject = getEntityIdObject(object);
			if (entityIdObject != null) {
				list.add(entityIdObject);
			}
		}
		return list;
	}
	
}