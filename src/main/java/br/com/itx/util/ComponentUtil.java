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

import br.com.itx.component.AbstractHome;

public final class ComponentUtil {
	
	private static final LogProvider log = Logging.getLogProvider(ComponentUtil.class);
	
	private ComponentUtil() { }

	/**
	 * Busca um componente pelo identificador
	 * @param componentId identificador do component
	 * @return componente com o nome solicitado ou null, 
	 * 		especialmente em testes de integra��o.
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
			ex.printStackTrace();
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
		} catch (IntrospectionException e) { }
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
			ex.printStackTrace();
			return null;
		}
	}
	
	public static Class<?> getType(Object component, String property) {
		PropertyDescriptor pd = getPropertyDescriptor(component, property);
		if (pd == null) { return null; }
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
			log.warn("Exception ao tentar ler atributo privado", e);
		}
		return returnObj;
	}	

	/**
	 * Metodo que recebe o nome de um home e devolve o getInstance() deste home, fazendo 
	 * o cast. Retorna uma exce��o caso o componente n�o seja encontrado.
	 * @param <C>
	 * @param homeName
	 * @return
	 */
	public static <C> C getInstance(String homeName) {
		AbstractHome<C> home = getComponent(homeName);
		if (home == null) {
			throw new IllegalArgumentException("O home '" + homeName + 
					"' n�o foi encontrado.");
		}
		return home.getInstance();
	}

	/**
	 * Metodo que devolve a instancia de um componente usando o 
	 * {@link org.jboss.seam.Component#getInstance(String) Component.getInstance}
	 * e fazendo cast para o tipo declarado.
	 * @param <C> O tipo declarado
	 * @param componentName Nome do componte
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <C> C getComponent(String componentName) {
		return (C) Component.getInstance(componentName);
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
	@SuppressWarnings("unchecked")
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
	@SuppressWarnings("unchecked")
	public static <C> C getComponent(String componentName, boolean create) {
		return (C) Component.getInstance(componentName, create);
	}		
	
	/**
	 * Retorna a nome do componente atraves da anota��o @Name
	 * @param clazz
	 * @return
	 */
	public static String getComponentName(Class<?> clazz) {
		Name annotationName = clazz.getAnnotation(Name.class);
		return annotationName.value();
	}
	
	/**
	 * Testa de um componente est� no contexto de conversa��o
	 * @param clazz
	 * @return
	 */
	public static boolean isOnConversationContext(Class<?> clazz) {
		String componentName = ComponentUtil.getComponentName(clazz);
		if (componentName != null) {
			return isOnConversationContext(componentName);
		} else {
			throw new IllegalArgumentException("Classe n�o possui @Name");
		}
	}	
	
	/**
	 * Testa de um componente est� no contexto de conversa��o
	 * @param name
	 * @return
	 */
	public static boolean isOnConversationContext(String name) {
		Object object = Contexts.getConversationContext().get(name);
		return object != null;
	}	
			
}