package br.com.infox.epp.layout.view;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import br.com.infox.epp.cdi.util.Beans;
import br.com.infox.epp.layout.dao.ResourceDao;
import br.com.infox.epp.layout.entity.Resource;

@FacesConverter("resourceConverter")
public class ResourceConverter implements Converter {

	
	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		return Beans.getReference(ResourceDao.class).findByCodigo(value);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		Resource resource = (Resource) value;
		return resource.getCodigo();
	}

}
