package br.com.infox.epp.processo.form.type;

import org.richfaces.renderkit.html.images.GradientAlignment;

import com.google.common.base.Optional;

import br.com.infox.epp.cdi.config.BeanManager;
import br.com.infox.epp.processo.form.FormData;
import br.com.infox.epp.processo.form.FormField;
import br.com.infox.epp.processo.form.TaskFormData;
import br.com.infox.epp.processo.form.variable.value.ValueType;
import br.com.infox.ibpm.variable.FragmentConfiguration;
import br.com.infox.ibpm.variable.FragmentConfigurationCollector;

public class FragmentFormType implements FormType{

	public static final String NAME = "fragment";
	public static final String PATH = "/Processo/form/fragment.xhtml";
    
    public FragmentFormType(){
    	
    }
    
	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public String getPath() {
		return PATH;
	}

	@Override
	public ValueType getValueType() {
		return ValueType.SERIALIZABLE;
	}

	@Override
	public Object convertToFormValue(Object value) {
		return value;
	}

	@Override
	public void performValue(FormField formField, FormData formData) {

		String codigoFragment = formField.getProperty("fragmentDefiniton", String.class);
		FragmentConfiguration fragmentConfiguration = BeanManager.INSTANCE
				.getReference(FragmentConfigurationCollector.class).getByCode(codigoFragment);
		formField.getProperties().put("fragmentPath", fragmentConfiguration.getPath());
		formField.getProperties().put("config", fragmentConfiguration);
		formField.setValue(Optional.fromNullable(formField.getValue())
		        .or(fragmentConfiguration.init(((TaskFormData)formData).getTaskInstance())));
	}

	@Override
	public void performUpdate(FormField formField, FormData formData) {
		  // do nothing
	}

	@Override
	public boolean isInvalid(FormField formField, FormData formData) {
		return false;
	}

	@Override
	public boolean isPersistable() {
		return true;
	}

}
