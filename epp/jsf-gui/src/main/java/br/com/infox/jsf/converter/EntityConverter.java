package br.com.infox.jsf.converter;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.persistence.EntityManager;

import br.com.infox.cdi.producer.EntityManagerProducer;
import br.com.infox.core.util.EntityUtil;

@FacesConverter("br.com.infox.jsf.converter.EntityConverter")
public class EntityConverter implements javax.faces.convert.Converter, Serializable {


    private static final long serialVersionUID = 1L;

    private EntityManager getEntityManager(){
        return EntityManagerProducer.getEntityManager();
    }
    
    public String getAsString(FacesContext facesContext, UIComponent cmp, Object value) throws ConverterException {
        if (value == null || (value instanceof String && ((String)value).isEmpty())) {
            return "";
        }
        if (!EntityUtil.isEntity(value)){
            throw new ConverterException(new FacesMessage("Must be an entity"));
        }
        
        Object identifier = EntityUtil.getIdentifier(value);
        return identifier==null?null:identifier.toString();
    }

    public Object getAsObject(FacesContext facesContext, UIComponent cmp, String value) throws ConverterException {
        if (value == null || value.length() == 0) {
            return null;
        }
        Class<?> type = cmp.getValueExpression("value").getType(facesContext.getELContext());
        Class<?> idPropertyType = EntityUtil.getId(type).getPropertyType();
        Serializable id;
        
        if (Integer.class.equals(idPropertyType) || int.class.equals(idPropertyType)){
            id = Integer.valueOf(value);
        } else if (Long.class.equals(idPropertyType) || long.class.equals(idPropertyType)){
            id = Long.valueOf(value);
        } else {
            id=value;
        }
        
        return getEntityManager().find(type, id);
    }

}
