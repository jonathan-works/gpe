package br.com.infox.jsf;

import javax.faces.component.UIComponent;

public interface AjaxRequestPreBuilder {
	AjaxRequestBuilder from(UIComponent component);

	AjaxRequestBuilder from(String clientId);
}