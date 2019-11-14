package br.com.infox.ibpm.node.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.infox.ibpm.node.converter.NodeNameFixer;

@FacesConverter(value = "nodeNameConverter")
public class NodeNameConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return NodeNameFixer.fixCharsInNodeName(value);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return value != null ? value.toString() : null;
    }
}
