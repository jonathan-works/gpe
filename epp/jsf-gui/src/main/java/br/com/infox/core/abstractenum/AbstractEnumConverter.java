package br.com.infox.core.abstractenum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("abstractEnumConverter")
public class AbstractEnumConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null && !value.isEmpty()) {
            try {
                Object className = component.getAttributes().get("enumClassName");
                Method method = Class.forName((String) className).getMethod("valueOf", String.class);
                return (AbstractEnum) method.invoke(null, value);
            } catch (NoSuchMethodException | SecurityException | ClassNotFoundException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                throw new ConverterException(new FacesMessage("Error converting abstractEnum"), e);
            }
        }
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value != null && value instanceof AbstractEnum) {
            return ((AbstractEnum) value).name();
        }
        return null;
    }

}