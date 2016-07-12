package br.com.infox.jsf.converter;

import java.util.HashMap;
import java.util.Map;

import javax.faces.FacesException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import br.com.infox.epp.processo.marcador.Marcador;

@ViewScoped
@ManagedBean(name = "br.com.infox.conveter.Marcador")
public class MarcadorConverter implements Converter {
    
    private Map<String, Marcador> marcadores = new HashMap<>();
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value != null) {
            return marcadores.get(value);
        }
        marcadores.clear();
        return null;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value instanceof Marcador) {
            Marcador marcador = (Marcador) value;
            marcadores.put(marcador.getCodigo(), marcador);
            return marcador.getCodigo();
        } else {
            throw new FacesException("Error converting  " + value);
        }
    }

}
