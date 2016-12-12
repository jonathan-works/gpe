package br.com.infox.seam.util;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.hibernate.AnnotationException;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.util.Reflections;

import br.com.infox.hibernate.util.HibernateUtil;
import br.com.infox.log.LogProvider;
import br.com.infox.log.Logging;

public final class ComponentUtil {

    private static final LogProvider LOG = Logging.getLogProvider(ComponentUtil.class);

    private ComponentUtil() {
    }

    /**
     * Busca um componente pelo identificador
     * 
     * @param componentId identificador do component
     * @return componente com o nome solicitado ou null, especialmente em testes
     *         de integração.
     */
    @Deprecated
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
     * 
     * @param component geralmente um UIForm, mas pode ser qualquer tipo de
     *        UIComponente
     */
    @Deprecated
    public static void clearChildren(UIComponent component) {
        if (component == null) {
            return;
        }
        if (component instanceof EditableValueHolder) {
            EditableValueHolder evh = (EditableValueHolder) component;
            evh.resetValue();
        }
        for (UIComponent c : component.getChildren()) {
            clearChildren(c);
        }
    }

    /**
     * Metodo que devolve um array com os PropertyDescriptors de uma classe
     * 
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

    public static Object getValue(Object component, String property) {
        Method getterMethod = Reflections.getGetterMethod(component.getClass(), property);
        if (getterMethod != null) {
            getterMethod.setAccessible(true);
            return Reflections.invokeAndWrap(getterMethod, component, new Object[0]);
        }
        return null;
    }

    public static void setValue(Object component, String property, Object value) {
        Method setterMethod = Reflections.getSetterMethod(component.getClass(), property);
        if (setterMethod != null) {
            Reflections.invokeAndWrap(setterMethod, component, value);
        }
    }

    /**
     * Metodo que devolve a instancia de um componente usando o
     * {@link org.jboss.seam.Component#getInstance(String)
     * Component.getInstance} e fazendo cast para o tipo declarado.
     * 
     * @param <C> O tipo declarado
     * @param componentName Nome do componte
     * @return
     */
    @SuppressWarnings(UNCHECKED)
    public static <C> C getComponent(String componentName) {
        return (C) Component.getInstance(componentName);
    }

    /**
     * Metodo que devolve a instancia de um componente usando o
     * {@link org.jboss.seam.Component#getInstance(String)
     * Component.getInstance} e fazendo cast para o tipo declarado.
     * 
     * @param <C> O tipo declarado
     * @param componentName Nome do componente
     * @param scopeType O Escopo do componente
     * @return
     */
    @SuppressWarnings(UNCHECKED)
    public static <C> C getComponent(String componentName, ScopeType scopeType) {
        return (C) Component.getInstance(componentName, scopeType);
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
    	object = HibernateUtil.removeProxy(object);
        String fieldName = getAnnotationField(object.getClass(), annotationClass);
        return getValue(object, fieldName);
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

}
