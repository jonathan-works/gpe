package br.com.infox.jsf;

import javax.faces.context.FacesContext;

public class AjaxRequestBuilderFactory {
	public static AjaxRequestPreBuilder create(FacesContext context) {
		return new AjaxRequestBuilderImpl(context);
	}

	public static AjaxRequestPreBuilder create() {
		return new AjaxRequestBuilderImpl(FacesContext.getCurrentInstance());
	}

	public static JSFunctionBuilder jsFunction() {
		return new JSFunctionBuilderImpl();
	}
}