package br.com.infox.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(CnpjConverter.CONVERTER_ID)
public class CnpjConverter implements Converter {
    public static final String CONVERTER_ID = "cnpjConverter";

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            value = value.replace(".", "").replace("-", "").replace("/", "");
        }
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        StringBuilder cnpj = new StringBuilder((String) value);
        cnpj.insert(2, '.'); // Após o 1º grupo (2 + 0 separador)
        cnpj.insert(6, '.'); // Após o 2º grupo (5 + 1 separador)
        cnpj.insert(10, '/'); // Após o 3º grupo (8 + 2 separadores)
        cnpj.insert(15, '-'); // Após o 4º grupo (12 + 3 separadores)
        return cnpj.toString();
    }
}
