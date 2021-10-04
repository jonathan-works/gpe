package br.com.infox.jsf.converter;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import com.google.common.base.Strings;

import br.com.infox.core.util.StringUtil;

@FacesConverter("bigDecimalConverter")
public class BigDecimalConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (StringUtil.isEmpty(value)) {
            return null;
        }
        return new BigDecimal(value.replace(".", "").replace(",", "."));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if(value != null) {
            BigDecimal bd = (BigDecimal) value;
            DecimalFormat df = null;
            if(bd.scale() > 0) {
                df = new DecimalFormat("#,###.".concat(Strings.repeat("0", bd.scale())));
            } else {
                df = new DecimalFormat("#,###");
            }
            return df.format(bd.doubleValue());
        }

        return null;
    }

}
