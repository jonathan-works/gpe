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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.apache.commons.beanutils.PropertyUtils;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;

import br.com.infox.core.constants.WarningConstants;

public final class ComponentUtil {
	
	private static final LogProvider LOG = Logging.getLogProvider(ComponentUtil.class);
	
	private ComponentUtil() { }

	/**
	 * Busca um componente pelo identificador
	 * @param componentId identificador do component
	 * @return componente com o nome solicitado ou null, 
	 * 		especialmente em testes de integração.
	 */
	public static UIComponent getUIComponent(String componentId) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (facesContext == null) {
			return null;
		}
		UIViewRoot viewRoot = facesContext.getViewRoot();
		if (viewRoot == null) {
			return null;
		}
		return viewRoot.findComponent(componentId);
	}

	/**
	 * Limpa os campos de um componente
	 * @param component geralmente um UIForm, mas pode ser qualquer tipo de UIComponente
	 */
	public static void clearChildren(UIComponent component) {
		if (component == null) {
			return;
		}
		if (component instanceof EditableValueHolder) {
			EditableValueHolder evh = (EditableValueHolder) component;
			evh.setValid(true);
			evh.setValue(null);
		}
		for (UIComponent c : component.getChildren()) {
			clearChildren(c);
		}
	}
	
	public static List<PropertyDescriptor> getProperties(Object component) {
		return getProperties(component.getClass());
	}

	public static List<PropertyDescriptor> getProperties(Class<?> component) {
		List<PropertyDescriptor> resp = new ArrayList<PropertyDescriptor>();
		try {
			PropertyDescriptor[] pds = getPropertyDescriptors(component);		
			for (int i = 0; i < pds.length; i++) {
				PropertyDescriptor pd = pds[i];
				if (!pd.getName().equals("class") && 
						!pd.getName().equals("bytes")) {
					resp.add(pd);
				}
			}
		} catch (Exception ex) {
		    LOG.error(".getProperties()", ex);
		}
		return resp;
	}
	
	/**
	 * Metodo que devolve um array com os PropertyDescriptors de uma classe
	 * @param clazz
	 * @return
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) {
		try {
			return Introspector.getBeanInfo(clazz).getPropertyDescriptors();
		} catch (IntrospectionException e) {
		    LOG.error(".getPropertyDescriptors()", e);
		}
		return new PropertyDescriptor[0];
	}
	
	public static PropertyDescriptor[] getPropertyDescriptors(Object component) {
		return getPropertyDescriptors(component.getClass());
	}
	
	public static PropertyDescriptor getPropertyDescriptor(Object component, 
			String property) {
		try {
			return PropertyUtils.getPropertyDescriptor(component, property);
		} catch (Exception ex) {
		    LOG.error(".getPropertyDescriptor()", ex);
			return null;
		}
	}
	
	public static Class<?> getType(Object component, String property) {
		PropertyDescriptor pd = getPropertyDescriptor(component, property);
		if (pd == null) { 
		    return null;
		}
		return getType(pd);
	}

	public static Class<?> getType(PropertyDescriptor pd) {
		return pd.getPropertyType();
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
	
	public static Object getValue(Object component, String property) {
		Method getterMethod = Reflections.getGetterMethod(component.getClass(), property);
		if (getterMethod != null) { 
			getterMethod.setAccessible(true);
			return Reflections.invokeAndWrap(getterMethod, component, new Object[0]);
		}
		return null;
	}

	public static Object getValue(Object component, PropertyDescriptor pd) {
		return Reflections.invokeAndWrap(pd.getReadMethod(), component, new Object[0]);
	}
	
	public static void setValue(Object component, String property, 
			Object value) {
		Method setterMethod = Reflections.getSetterMethod(component.getClass(), property);
		if (setterMethod != null) {
			Reflections.invokeAndWrap(setterMethod, component, value);
		}
	}

	public static void setValue(Object component, PropertyDescriptor pd,
			Object value) {
		Reflections.invokeAndWrap(pd.getWriteMethod(), component, value);
	}
	
	public static Object getValuePrivateField(Object component, String fieldName) {
		Object returnObj = null;
		try {
			Field f = component.getClass().getDeclaredField(fieldName);
			f.setAccessible(true);
			returnObj = f.get(component);
			f.setAccessible(false);
		}
		catch (Exception e) {
			LOG.warn("Exception ao tentar ler atributo privado", e);
		}
		return returnObj;
	}	

	/**
	 * Metodo que devolve a instancia de um componente usando o 
	 * {@link org.jboss.seam.Component#getInstance(String) Component.getInstance}
	 * e fazendo cast para o tipo declarado.
	 * @param <C> O tipo declarado
	 * @param componentName Nome do componte
	 * @return
	 */
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public static <C> C getComponent(String componentName) {
		return (C) Component.getInstance(componentName);
	}
	
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public static <C> C getComponent(Class<C> componentClass) {
	    return (C) Component.getInstance(componentClass);
	}
	
	/**
	 * Metodo que devolve a instancia de um componente usando o 
	 * {@link org.jboss.seam.Component#getInstance(String) Component.getInstance}
	 * e fazendo cast para o tipo declarado.
	 * @param <C> O tipo declarado
	 * @param componentName Nome do componente
	 * @param scopeType O Escopo do componente
	 * @return
	 */
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public static <C> C getComponent(String componentName, ScopeType scopeType) {
		return (C) Component.getInstance(componentName, scopeType);
	}		
	
	/**
	 * Retorna true se algum dos objetos for null
	 * @param objects
	 * @return
	 */
	public static boolean containsNullObject(Object... objects) {
		if (objects != null) {
			for (Object object : objects) {
				if (object == null) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Metodo que devolve a instancia de um componente usando o 
	 * {@link org.jboss.seam.Component#getInstance(String, boolean) Component.getInstance}
	 * e fazendo cast para o tipo declarado.
	 * @param <C> O tipo declarado
	 * @param componentName Nome do componte
	 * @return
	 */
	@SuppressWarnings(WarningConstants.UNCHECKED)
	public static <C> C getComponent(String componentName, boolean create) {
		return (C) Component.getInstance(componentName, create);
	}		
	
	/**
	 * Retorna a nome do componente atraves da anotação @Name
	 * @param clazz
	 * @return
	 */
	public static String getComponentName(Class<?> clazz) {
		Name annotationName = clazz.getAnnotation(Name.class);
		return annotationName.value();
	}
	
	/**
	 * Testa de um componente está no contexto de conversação
	 * @param clazz
	 * @return
	 */
	public static boolean isOnConversationContext(Class<?> clazz) {
		String componentName = ComponentUtil.getComponentName(clazz);
		if (componentName != null) {
			return isOnConversationContext(componentName);
		} else {
			throw new IllegalArgumentException("Classe não possui @Name");
		}
	}	
	
	/**
	 * Testa de um componente está no contexto de conversação
	 * @param name
	 * @return
	 */
	public static boolean isOnConversationContext(String name) {
		Object object = Contexts.getConversationContext().get(name);
		return object != null;
	}	
			
}