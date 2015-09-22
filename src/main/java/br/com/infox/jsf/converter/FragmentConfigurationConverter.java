package br.com.infox.jsf.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

import org.jboss.seam.annotations.Name;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.ibpm.variable.FragmentConfiguration;
import br.com.infox.ibpm.variable.FragmentConfigurationCollector;

@Name(FragmentConfigurationConverter.NAME)
@FacesConverter(FragmentConfigurationConverter.NAME)
public class FragmentConfigurationConverter implements Converter {

    public static final String NAME = "fragmentConfigurationConverter";

    @Override
    public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
        return BeanManager.INSTANCE.getReference(FragmentConfigurationCollector.class)
                .getByCode(arg2);
    }

    @Override
    public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
        return ((FragmentConfiguration) arg2).getCode();
    }

}
