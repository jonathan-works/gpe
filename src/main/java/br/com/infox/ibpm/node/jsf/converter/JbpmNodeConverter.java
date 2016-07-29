package br.com.infox.ibpm.node.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.jbpm.graph.def.Node;

import br.com.infox.ibpm.process.definition.fitter.NodeFitter;

@FacesConverter("jbpmNodeConverter")
public class JbpmNodeConverter implements Converter {
	
	@Inject
	private NodeFitter nodeFitter;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return nodeFitter.getNodeByKey(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return ((Node) value).getKey();
	}
}
