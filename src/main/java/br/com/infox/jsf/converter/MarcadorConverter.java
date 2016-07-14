package br.com.infox.jsf.converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.infox.epp.processo.marcador.Marcador;

@FacesConverter("br.com.infox.converter.marcador")
public class MarcadorConverter implements Converter {
    
    @Override
    @SuppressWarnings("unchecked")
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Map<String, Map<String, Marcador>> marcadoresView = (Map<String, Map<String, Marcador>>) context.getViewRoot().getViewMap().get("marcadores");
        Map<String, Marcador> mapMarcadores = marcadoresView.get(component.getClientId());
        if (value != null && mapMarcadores != null) {
            return mapMarcadores.get(value);
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (!context.getViewRoot().getViewMap().containsKey("marcadores")) {
            context.getViewRoot().getViewMap().put("marcadores", Collections.synchronizedMap(new HashMap<String, Map<String, Marcador>>()));
        }
        Map<String, Map<String, Marcador>> marcadoresView = (Map<String, Map<String, Marcador>>) context.getViewRoot().getViewMap().get("marcadores");
        if (!marcadoresView.containsKey(component.getClientId())) {
            marcadoresView.put(component.getClientId(), new HashMap<String, Marcador>());
        }
        if (value instanceof Marcador) {
            Marcador marcador = (Marcador) value;
            marcadoresView.get(component.getClientId()).put(marcador.getCodigo(), marcador);
            return marcador.getCodigo();
        }
        return null;
    }

}
