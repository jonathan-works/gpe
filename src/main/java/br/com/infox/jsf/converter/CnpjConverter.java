package br.com.infox.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;

@Name(CnpjConverter.CONVERTER_ID)
@FacesConverter(CnpjConverter.CONVERTER_ID)
public class CnpjConverter implements Converter {
	
    public static final String CONVERTER_ID = "cnpjConverter";
    
    public static CnpjConverter getInstance(){
    	return (CnpjConverter) Component.getInstance(CnpjConverter.class);
    }
    
    public String format(String value){
    	 StringBuilder cnpj = new StringBuilder(value);
         cnpj.insert(2, '.'); // Após o 1º grupo (2 + 0 separador)
         cnpj.insert(6, '.'); // Após o 2º grupo (5 + 1 separador)
         cnpj.insert(10, '/'); // Após o 3º grupo (8 + 2 separadores)
         cnpj.insert(15, '-'); // Após o 4º grupo (12 + 3 separadores)
         return cnpj.toString();
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            value = value.replaceAll("\\D+", "");
        }
        return value;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return format((String) value);
    }
}
