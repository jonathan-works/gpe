package br.com.itx.util;

import static br.com.infox.constants.WarningConstants.UNCHECKED;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Reflections;

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

}
