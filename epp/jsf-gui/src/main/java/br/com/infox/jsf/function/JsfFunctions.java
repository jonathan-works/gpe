package br.com.infox.jsf.function;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public final class JsfFunctions {

    private JsfFunctions() {
    }

    public static Object get(Object value, Object defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static Integer splitLength(String obj, String token) {
        if (obj == null) {
            return 0;
        }
        return obj.split(token).length;
    }
    
    @SuppressWarnings("rawtypes")
    public static List truncateList(List list, int first, int ammount){
        /*
         * Quando especificar ammount 0, deve retornar uma lista vazia
         * Quando especificar ammount maior que o tamanho da lista, deve considerar que vai copiar a lista até o último nó
         * Quando o first for menor que 0 deve dar index out of bounds exception 
         */
        if (list == null || ammount < 1) 
            return Collections.emptyList();
        int size = list.size();
        int fromIndex = Math.min(first, size);
        int toIndex = Math.min(fromIndex+ammount, size);
        if (toIndex > size || fromIndex > toIndex)
            return Collections.emptyList();
        
        return list.subList(fromIndex, toIndex);
    }
    
    public static String clientId(String id) {
        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent component = findComponent(context, id);
        return component != null ? component.getClientId(context) : null;
    }
    
    public static UIComponent findComponent(String id){
        FacesContext context = FacesContext.getCurrentInstance();
        UIComponent component = findComponent(context, id);
        return component;
    }
    
    private static UIComponent findComponent(FacesContext context, String id) {
        if (id != null) {
            UIComponent contextComponent = UIComponent.getCurrentComponent(context);
            if (contextComponent == null) {
                contextComponent = context.getViewRoot();
            }
            
            UIComponent component = findComponentFor(contextComponent, id);

            if (component != null) {
                return component;
            }
        }

        return null;
    }

    private static UIComponent findComponentFor(UIComponent component, String id) {
        if (id == null) {
            throw new NullPointerException("id is null!");
        }

        if (id.length() == 0) {
            return null;
        }

        UIComponent target = null;
        UIComponent parent = component;
        UIComponent root = component;

        while ((null == target) && (null != parent)) {
            target = parent.findComponent(id);
            root = parent;
            parent = parent.getParent();
        }

        if (null == target) {
            target = findUIComponentBelow(root, id);
        }

        return target;
    }
    private static UIComponent findUIComponentBelow(UIComponent root, String id) {
        UIComponent target = null;

        for (Iterator<UIComponent> iter = root.getFacetsAndChildren(); iter.hasNext();) {
            UIComponent child = (UIComponent) iter.next();

            if (child instanceof NamingContainer) {
                try {
                    target = child.findComponent(id);
                } catch (IllegalArgumentException iae) {
                    continue;
                }
            }

            if (target == null) {
                if ((child.getChildCount() > 0) || (child.getFacetCount() > 0)) {
                    target = findUIComponentBelow(child, id);
                }
            }

            if (target != null) {
                break;
            }
        }

        return target;
    }
}