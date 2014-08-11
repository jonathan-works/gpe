package br.com.infox.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(CpfConverter.CONVERTER_ID)
public class CpfConverter implements Converter {
    public static final String CONVERTER_ID = "cpfConverter";

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            value = value.replace(".", "").replace("-", "");
        }
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        StringBuilder cpf = new StringBuilder((String) value);
        cpf.insert(3, '.'); // Após o 1º grupo (3 + 0 ponto)
        cpf.insert(7, '.'); // Após o 2º grupo (3 + 1 ponto)
        cpf.insert(11, '-'); // Após o 3º grupo (3 + 2 pontos)
        return cpf.toString();
    }
}
