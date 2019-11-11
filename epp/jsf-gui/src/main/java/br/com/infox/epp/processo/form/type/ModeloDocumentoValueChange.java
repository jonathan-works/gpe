package br.com.infox.epp.processo.form.type;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ValueChangeEvent;
import javax.faces.event.ValueChangeListener;

import lombok.Getter;

public class ModeloDocumentoValueChange implements ValueChangeListener{
	@Getter
	private Object oldValue;
	@Getter
	private Object newValue;

	@Override
	public void processValueChange(ValueChangeEvent event) throws AbortProcessingException {
		oldValue = event.getOldValue();
		newValue = event.getNewValue();
	}

}
