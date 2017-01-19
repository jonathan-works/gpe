package br.com.infox.ibpm.node.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.jbpm.graph.def.Node;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.ibpm.process.definition.fitter.NodeFitter;

@FacesConverter("jbpmNodeConverter")
public class JbpmNodeConverter implements Converter {
	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null) {
			return null;
		}
		return getNodeFitter().getNodeByKey(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return ((Node) value).getKey();
	}
	
	private NodeFitter getNodeFitter() {
	    return BeanManager.INSTANCE.getReference(NodeFitter.class);
	}
}
