package br.com.infox.jsf.converter;

import java.math.BigDecimal;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.infox.core.util.StringUtil;

@FacesConverter("inputNumberBigDecimalConverter")
public class InputNumberBigDecimalConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        return new BigDecimal(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value == null ? null : value.toString();
    }

}
